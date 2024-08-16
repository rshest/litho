/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.rendercore

import android.content.Context
import android.util.SparseArray
import androidx.annotation.IdRes
import com.facebook.rendercore.extensions.ExtensionState
import com.facebook.rendercore.utils.CommonUtils.getSectionNameForTracing

/**
 * A RenderUnit represents a single rendering primitive for RenderCore. Every RenderUnit has to
 * define at least a createContent method to allocate the RenderUnit content (View or Drawable) via
 * the [ContentAllocator] it returns from getContentAllocator method. That content will be
 * automatically recycled by RenderCore based on their concrete type.
 *
 * A RenderUnit should in most cases declare how it intends to bind data returning Binders from its
 * mountUnmountFunctions callback or from the attachDetachFunctions callback.
 *
 * Immutability: RenderUnits should be immutable! Continuing to change them after they are built and
 * given to RenderCore (e.g. via RenderState) is not safe.
 */
abstract class RenderUnit<MOUNT_CONTENT : Any>
@JvmOverloads
constructor(
    type: RenderType,
    fixedMountBinders: List<DelegateBinder<*, in MOUNT_CONTENT, *>>?,
    optionalMountBinders: List<DelegateBinder<*, in MOUNT_CONTENT, *>>,
    attachBinders: List<DelegateBinder<*, in MOUNT_CONTENT, *>>,
    extras: SparseArray<Any?>? = null
) {
  enum class RenderType {
    DRAWABLE,
    VIEW
  }

  val renderType: RenderType

  // These maps are used to match a binder with its Binder class.
  // Every RenderUnit should have only one Binder per type.
  private var optionalMountBinderTypeToDelegateMap:
      MutableMap<Class<*>, DelegateBinder<*, in MOUNT_CONTENT, *>>? =
      null
  private var optionalMountBinders: MutableList<DelegateBinder<*, in MOUNT_CONTENT, *>>? = null

  // Fixed mount binders are binders that are always there for a given RenderUnit type, and they're
  // always in the same order.
  private val fixedMountBinders: List<DelegateBinder<*, in MOUNT_CONTENT, *>>?
  private var attachBinderTypeToDelegateMap:
      MutableMap<Class<*>, DelegateBinder<*, in MOUNT_CONTENT, *>>? =
      null
  private var attachBinders: MutableList<DelegateBinder<*, in MOUNT_CONTENT, *>>? = null

  // Extra data associated with RenderUnit.
  // The keys must be unique integers generated by Android resource ID system (via XML).
  private var extras: SparseArray<Any?>? = null

  constructor(
      renderType: RenderType
  ) : this(
      renderType,
      emptyList<DelegateBinder<*, in MOUNT_CONTENT, *>>(),
      emptyList<DelegateBinder<*, in MOUNT_CONTENT, *>>(),
      emptyList<DelegateBinder<*, in MOUNT_CONTENT, *>>())

  init {
    check(!(fixedMountBinders != null && fixedMountBinders.size > MAX_FIXED_MOUNT_BINDERS_COUNT)) {
      "Too many fixed mount binders. Max is $MAX_FIXED_MOUNT_BINDERS_COUNT"
    }
    renderType = type
    this.fixedMountBinders = fixedMountBinders
    for (i in optionalMountBinders.indices) {
      val binder = optionalMountBinders[i]
      addOptionalMountBinder(binder)
    }
    for (i in attachBinders.indices) {
      val binder = attachBinders[i]
      addAttachBinder(binder)
    }
    if (extras != null && extras.size() > 0) {
      this.extras = extras.clone()
    }
  }

  abstract val contentAllocator: ContentAllocator<MOUNT_CONTENT>

  /** @return a unique id identifying this RenderUnit in the tree of Node it is part of. */
  abstract val id: Long
  open val debugKey: String?
    /** Returns a key that can be used for debugging purposes. */
    get() = id.toString()

  open val renderContentType: Class<*>
    get() = javaClass

  open fun onStartUpdateRenderUnit(): Unit = Unit

  open fun onEndUpdateRenderUnit(): Unit = Unit

  open val description: String
    get() = getSectionNameForTracing(javaClass)

  /** Returns extra data associated with this RenderUnit for given key. */
  open fun <T> getExtra(@IdRes key: Int): T? {
    return extras?.get(key) as T?
  }

  /**
   * Adds an [DelegateBinder] that will be invoked with the other mount/unmount binders. Can be used
   * to add generic functionality (e.g. accessibility) to a RenderUnit.
   *
   * NB: This method should only be called while initially configuring the RenderUnit. See the
   * class-level javadocs about immutability.
   */
  open fun addOptionalMountBinder(binder: DelegateBinder<*, in MOUNT_CONTENT, *>) {
    val mountBinders = optionalMountBinders ?: ArrayList()
    if (optionalMountBinders == null) {
      optionalMountBinders = mountBinders
      check(optionalMountBinderTypeToDelegateMap == null) {
        "Binder Map and Binder List out of sync!"
      }
      optionalMountBinderTypeToDelegateMap = HashMap()
    }
    optionalMountBinderTypeToDelegateMap?.let { addBinder(it, mountBinders, binder) }
  }

  /**
   * Adds [DelegateBinder]s that will be invoked with the other mount/unmount binders. Can be used
   * to add generic functionality (e.g. accessibility) to a RenderUnit.
   *
   * NB: This method should only be called while initially configuring the RenderUnit. See the
   * class-level javadocs about immutability.
   */
  @SafeVarargs
  fun addOptionalMountBinders(vararg binders: DelegateBinder<*, in MOUNT_CONTENT, *>) {
    for (i in binders.indices) {
      addOptionalMountBinder(binders[i])
    }
  }

  /**
   * Adds an [DelegateBinder] that will be invoked with the other attach/detach binders. Can be used
   * to add generic functionality (e.g. Dynamic Props) to a RenderUnit
   *
   * NB: This method should only be called while initially configuring the RenderUnit. See the
   * class-level javadocs about immutability.
   */
  open fun addAttachBinder(binder: DelegateBinder<*, in MOUNT_CONTENT, *>) {
    val binders = attachBinders ?: ArrayList()
    if (attachBinders == null) {
      attachBinders = binders
      check(attachBinderTypeToDelegateMap == null) { "Binder Map and Binder List out of sync!" }
      attachBinderTypeToDelegateMap = HashMap()
    }
    attachBinderTypeToDelegateMap?.let { addBinder(it, binders, binder) }
  }

  /**
   * Adds an [DelegateBinder]s that will be invoked with the other attach/detach binders. Can be
   * used to add generic functionality (e.g. Dynamic Props) to a RenderUnit
   *
   * NB: This method should only be called while initially configuring the RenderUnit. See the
   * class-level javadocs about immutability.
   */
  @SafeVarargs
  fun addAttachBinders(vararg binders: DelegateBinder<*, in MOUNT_CONTENT, *>) {
    for (i in binders.indices) {
      addAttachBinder(binders[i])
    }
  }

  /**
   * Override this method to indicate if a [RenderUnit] has nested [RenderTreeHost]s, it will ensure
   * that they are notified when this [RenderUnit]'s bounds change and visibility events are
   * processed correctly for them.
   *
   * @return `true` to ensure nested [RenderTreeHost]s are notified about parent's bounds change,
   *   otherwise `false`
   */
  open fun doesMountRenderTreeHosts(): Boolean {
    return false
  }

  /** Bind all fixed mountUnmount binder functions. */
  private fun mountFixedBinders(
      context: Context,
      content: MOUNT_CONTENT,
      layoutData: Any?,
      bindData: BindData,
      tracer: Systracer
  ) {
    val isTracing = tracer.isTracing()
    val fixedMountBindersSize = fixedMountBinders?.size ?: return
    if (isTracing) {
      tracer.beginSection(sectionName("$description:mount-fixed"))
    }

    for (i in 0 until fixedMountBindersSize) {
      val binder = fixedMountBinders[i]
      if (isTracing) {
        tracer.beginSection(sectionName(binder.description))
      }
      try {
        val binderBindData = binder.bind(context, content, layoutData)
        bindData.setFixedBindersBindData(binderBindData, i, fixedMountBindersSize)
      } catch (exception: Exception) {
        throw RenderUnitOperationException(
            renderUnit = this,
            message = "Exception binding fixed mount binder: ${binder.description}",
            cause = exception)
      } finally {
        if (isTracing) {
          tracer.endSection()
        }
      }
    }

    if (isTracing) {
      tracer.endSection()
    }
  }

  /** Bind all mountUnmount binder functions. */
  open fun mountBinders(
      context: Context,
      content: MOUNT_CONTENT,
      layoutData: Any?,
      bindData: BindData,
      tracer: Systracer
  ) {
    mountFixedBinders(context, content, layoutData, bindData, tracer)
    val optionalMountBinders = optionalMountBinders
    val isTracing = tracer.isTracing()
    val optionalMountBindersSize = optionalMountBinders?.size ?: return
    if (isTracing) {
      tracer.beginSection(sectionName("$description:mount-optional"))
    }
    for (i in 0 until optionalMountBindersSize) {
      val binder = optionalMountBinders[i]
      if (isTracing) {
        tracer.beginSection(sectionName(binder.description))
      }

      try {
        val binderBindData = binder.bind(context, content, layoutData)
        bindData.setOptionalMountBindersBindData(
            binderBindData, binder.binder.javaClass, optionalMountBindersSize)
      } catch (exception: Exception) {
        throw RenderUnitOperationException(
            renderUnit = this,
            message = "Exception while mounting optional mount binder: ${binder.description}",
            cause = exception)
      } finally {
        if (isTracing) {
          tracer.endSection()
        }
      }
    }
    if (isTracing) {
      tracer.endSection()
    }
  }

  /** Unbind all fixed mountUnmount binder functions. */
  private fun unmountFixedBinders(
      context: Context,
      content: MOUNT_CONTENT,
      layoutData: Any?,
      bindData: BindData,
      tracer: Systracer
  ) {
    val isTracing = tracer.isTracing()
    fixedMountBinders ?: return
    if (isTracing) {
      tracer.beginSection(sectionName("$description:unmount-fixed"))
    }
    for (i in fixedMountBinders.indices.reversed()) {
      val binder = fixedMountBinders[i] as DelegateBinder<*, in MOUNT_CONTENT, Any>
      if (isTracing) {
        tracer.beginSection(sectionName(binder.description))
      }
      try {
        binder.unbind(context, content, layoutData, bindData.removeFixedBinderBindData(i))
      } catch (exception: Exception) {
        throw RenderUnitOperationException(
            renderUnit = this,
            message = "Exception while unmounting fixed binder: ${binder.description}",
            cause = exception)
      } finally {
        if (isTracing) {
          tracer.endSection()
        }
      }
    }
    if (isTracing) {
      tracer.endSection()
    }
  }

  /** Unbind all mountUnmount binder functions. */
  open fun unmountBinders(
      context: Context,
      content: MOUNT_CONTENT,
      layoutData: Any?,
      bindData: BindData,
      tracer: Systracer
  ) {
    optionalMountBinders?.let { optionalMountBinders ->
      val isTracing = tracer.isTracing()
      if (isTracing) {
        tracer.beginSection(sectionName("$description:unmount-optional"))
      }
      for (i in optionalMountBinders.indices.reversed()) {
        val binder = optionalMountBinders[i] as DelegateBinder<*, in MOUNT_CONTENT, Any>
        if (isTracing) {
          tracer.beginSection(sectionName(binder.description))
        }

        try {
          binder.unbind(
              context,
              content,
              layoutData,
              bindData.removeOptionalMountBindersBindData(binder.binder.javaClass))
        } catch (exception: Exception) {
          throw RenderUnitOperationException(
              renderUnit = this,
              message =
                  "Exception while unmounting optional binder: [$description] ${binder.description}",
              cause = exception)
        } finally {
          if (isTracing) {
            tracer.endSection()
          }
        }
      }
      if (isTracing) {
        tracer.endSection()
      }
    }
    unmountFixedBinders(context, content, layoutData, bindData, tracer)
  }

  /** Bind all attachDetach binder functions. */
  open fun attachBinders(
      context: Context,
      content: MOUNT_CONTENT,
      layoutData: Any?,
      bindData: BindData,
      tracer: Systracer
  ) {
    val attachBinders = attachBinders ?: return
    val isTracing = tracer.isTracing()
    val attachBindersSize = attachBinders.size
    if (isTracing) {
      tracer.beginSection(sectionName("$description:attach"))
    }
    for (i in 0 until attachBindersSize) {
      val binder = attachBinders[i]
      if (isTracing) {
        tracer.beginSection(sectionName(binder.description))
      }
      val binderBindData = binder.bind(context, content, layoutData)
      bindData.setAttachBindersBindData(binderBindData, binder.binder.javaClass, attachBindersSize)
      if (isTracing) {
        tracer.endSection()
      }
    }
    if (isTracing) {
      tracer.endSection()
    }
  }

  /** Unbind all attachDetach binder functions. */
  open fun detachBinders(
      context: Context,
      content: MOUNT_CONTENT,
      layoutData: Any?,
      bindData: BindData,
      tracer: Systracer
  ) {
    val attachBinders = attachBinders ?: return
    val isTracing = tracer.isTracing()
    if (isTracing) {
      tracer.beginSection(sectionName("$description:detach"))
    }
    for (i in attachBinders.indices.reversed()) {
      val binder = attachBinders[i] as DelegateBinder<*, in MOUNT_CONTENT, Any>
      if (isTracing) {
        tracer.beginSection(sectionName(binder.description))
      }
      binder.unbind(
          context,
          content,
          layoutData,
          bindData.removeAttachBindersBindData(binder.binder.javaClass))
      if (isTracing) {
        tracer.endSection()
      }
    }
    if (isTracing) {
      tracer.endSection()
    }
  }

  /**
   * Unbind and rebind all binders which should update compared to a previous (i.e. current)
   * RenderUnit.
   */
  open fun updateBinders(
      context: Context,
      content: MOUNT_CONTENT,
      currentRenderUnit: RenderUnit<MOUNT_CONTENT>,
      currentLayoutData: Any?,
      newLayoutData: Any?,
      mountDelegate: MountDelegate?,
      bindData: BindData,
      isAttached: Boolean,
      tracer: Systracer
  ) {
    val isTracing = tracer.isTracing()
    val attachBindersForBind: MutableList<DelegateBinder<*, in MOUNT_CONTENT, *>> =
        ArrayList(sizeOrZero(attachBinders))
    val attachBindersForUnbind: MutableList<DelegateBinder<*, in MOUNT_CONTENT, *>> =
        ArrayList(sizeOrZero(currentRenderUnit.attachBinders))
    val optionalMountBindersForBind: MutableList<DelegateBinder<*, in MOUNT_CONTENT, *>> =
        ArrayList(sizeOrZero(optionalMountBinders))
    val optionalMountBindersForUnbind: MutableList<DelegateBinder<*, in MOUNT_CONTENT, *>> =
        ArrayList(sizeOrZero(currentRenderUnit.optionalMountBinders))

    // 1. Resolve fixed mount binders which should update.
    val fixedMountBindersToUpdate =
        try {
          resolveFixedMountBindersToUpdate(
              currentRenderUnit.fixedMountBinders,
              fixedMountBinders,
              currentLayoutData,
              newLayoutData)
        } catch (exception: Exception) {
          throw RenderUnitOperationException(
              renderUnit = this,
              message = "Exception resolving fixed mount binders to update",
              cause = exception)
        }

    // 2. Diff the binders to resolve what's to bind/unbind.
    resolveBindersToUpdate(
        currentRenderUnit.attachBinders,
        attachBinders,
        currentRenderUnit.attachBinderTypeToDelegateMap,
        currentLayoutData,
        newLayoutData,
        attachBindersForBind,
        attachBindersForUnbind)
    resolveBindersToUpdate(
        currentRenderUnit.optionalMountBinders,
        optionalMountBinders,
        currentRenderUnit.optionalMountBinderTypeToDelegateMap,
        currentLayoutData,
        newLayoutData,
        optionalMountBindersForBind,
        optionalMountBindersForUnbind)
    val extensionStatesToUpdate: List<ExtensionState<*>>? =
        mountDelegate?.collateExtensionsToUpdate(
            currentRenderUnit, currentLayoutData, this, newLayoutData, tracer)

    // 3. Unbind all attach binders which should update (only if currently attached).
    if (isAttached) {
      if (mountDelegate != null && extensionStatesToUpdate != null) {
        MountDelegate.onUnbindItemWhichRequiresUpdate(
            extensionStatesToUpdate,
            currentRenderUnit,
            currentLayoutData,
            this,
            newLayoutData,
            content,
            tracer)
      }
      if (isTracing) {
        tracer.beginSection(sectionName("$description:detach"))
      }
      for (i in attachBindersForUnbind.indices.reversed()) {
        val binder = attachBindersForUnbind[i] as DelegateBinder<*, in MOUNT_CONTENT, Any>
        if (isTracing) {
          tracer.beginSection(sectionName(binder.description))
        }
        binder.unbind(
            context,
            content,
            currentLayoutData,
            bindData.removeAttachBindersBindData(binder.binder.javaClass))
        if (isTracing) {
          tracer.endSection()
        }
      }
      if (isTracing) {
        tracer.endSection()
      }
    }

    // 4. Unbind all optional and fixed mount binders which should update.
    if (mountDelegate != null && extensionStatesToUpdate != null) {
      MountDelegate.onUnmountItemWhichRequiresUpdate(
          extensionStatesToUpdate,
          currentRenderUnit,
          currentLayoutData,
          this,
          newLayoutData,
          content,
          tracer)
    }
    if (isTracing) {
      tracer.beginSection(sectionName("$description:unmount-optional"))
    }
    for (i in optionalMountBindersForUnbind.indices.reversed()) {
      val binder = optionalMountBindersForUnbind[i] as DelegateBinder<*, in MOUNT_CONTENT, Any>
      if (isTracing) {
        tracer.beginSection(sectionName(binder.description))
      }
      binder.unbind(
          context,
          content,
          currentLayoutData,
          bindData.removeOptionalMountBindersBindData(binder.binder.javaClass))
      if (isTracing) {
        tracer.endSection()
      }
    }
    if (isTracing) {
      tracer.endSection()
    }
    if (fixedMountBindersToUpdate != 0L) {
      if (isTracing) {
        tracer.beginSection(sectionName("$description:unmount-fixed"))
      }
      fixedMountBinders?.let { mountBinders ->
        for (i in mountBinders.indices.reversed()) {
          if (fixedMountBindersToUpdate and (0x1L shl i) != 0L) {
            val binder =
                currentRenderUnit.fixedMountBinders?.get(i)
                    as DelegateBinder<*, in MOUNT_CONTENT, Any>? ?: continue
            if (isTracing) {
              tracer.beginSection(sectionName(binder.description))
            }
            binder.unbind(
                context, content, currentLayoutData, bindData.removeFixedBinderBindData(i))
            if (isTracing) {
              tracer.endSection()
            }
          }
        }
      }
      if (isTracing) {
        tracer.endSection()
      }
    }

    // 5. Rebind all fixed and optional mount binders which did update.
    if (fixedMountBindersToUpdate != 0L) {
      val fixedMountBindersSize = fixedMountBinders?.size ?: 0
      if (isTracing) {
        tracer.beginSection(sectionName("$description:mount-fixed"))
      }
      for (i in 0 until fixedMountBindersSize) {
        if (fixedMountBindersToUpdate and (0x1L shl i) != 0L) {
          val binder = fixedMountBinders?.get(i) ?: continue
          if (isTracing) {
            tracer.beginSection(sectionName(binder.description))
          }
          val binderBindData = binder.bind(context, content, newLayoutData)
          bindData.setFixedBindersBindData(binderBindData, i, fixedMountBindersSize)
          if (isTracing) {
            tracer.endSection()
          }
        }
      }
      if (isTracing) {
        tracer.endSection()
      }
    }
    val optionalMountBindersSize = optionalMountBinders?.size ?: 0
    if (isTracing) {
      tracer.beginSection(sectionName("$description:mount-optional"))
    }
    for (i in optionalMountBindersForBind.indices) {
      val binder = optionalMountBindersForBind[i]
      if (isTracing) {
        tracer.beginSection(sectionName(binder.description))
      }
      val binderBindData = binder.bind(context, content, newLayoutData)
      bindData.setOptionalMountBindersBindData(
          binderBindData, binder.binder.javaClass, optionalMountBindersSize)
      if (isTracing) {
        tracer.endSection()
      }
    }
    if (isTracing) {
      tracer.endSection()
    }
    if (mountDelegate != null && extensionStatesToUpdate != null) {
      MountDelegate.onMountItemWhichRequiresUpdate(
          extensionStatesToUpdate,
          currentRenderUnit,
          currentLayoutData,
          this,
          newLayoutData,
          content,
          tracer)
    }

    // 6. Rebind all attach binders which did update.
    val attachBindersSize = attachBinders?.size ?: 0
    if (isTracing) {
      tracer.beginSection(sectionName("$description:attach"))
    }
    for (i in attachBindersForBind.indices) {
      val binder = attachBindersForBind[i]
      if (isTracing) {
        tracer.beginSection(sectionName(binder.description))
      }
      val binderBindData = binder.bind(context, content, newLayoutData)
      bindData.setAttachBindersBindData(binderBindData, binder.binder.javaClass, attachBindersSize)
      if (isTracing) {
        tracer.endSection()
      }
    }
    if (isTracing) {
      tracer.endSection()
    }
    if (mountDelegate != null && extensionStatesToUpdate != null) {
      MountDelegate.onBindItemWhichRequiresUpdate(
          extensionStatesToUpdate,
          currentRenderUnit,
          currentLayoutData,
          this,
          newLayoutData,
          content,
          tracer)
    }
  }

  open fun shouldUpdate(newRenderUnit: RenderUnit<MOUNT_CONTENT>): Boolean = this !== newRenderUnit

  open fun <T : Binder<*, *, *>?> findAttachBinderByClass(klass: Class<T>): T? {
    return attachBinderTypeToDelegateMap?.get(klass)?.binder as T?
  }

  open fun containsAttachBinder(delegateBinder: DelegateBinder<*, *, *>): Boolean {
    return attachBinderTypeToDelegateMap?.containsKey(delegateBinder.binder.javaClass) ?: false
  }

  open fun containsOptionalMountBinder(delegateBinder: DelegateBinder<*, *, *>): Boolean {
    return optionalMountBinderTypeToDelegateMap?.containsKey(delegateBinder.binder.javaClass)
        ?: false
  }

  /**
   * A binder is a pair of data Model and [Binder]. The binder will bind the model to a matching
   * content type defined.
   */
  class DelegateBinder<MODEL, CONTENT, BIND_DATA : Any>
  private constructor(private val model: MODEL, binder: Binder<MODEL, CONTENT, BIND_DATA>) {
    val binder: Binder<MODEL, CONTENT, BIND_DATA>

    init {
      this.binder = binder
    }

    val delegatedBinderClass: Class<*>
      /** Returns the class for the binder to which it delegates. */
      get() = binder.javaClass

    fun shouldUpdate(
        previous: DelegateBinder<MODEL, CONTENT, BIND_DATA>,
        currentLayoutData: Any?,
        nextLayoutData: Any?
    ): Boolean {
      return binder.shouldUpdate(previous.model, model, currentLayoutData, nextLayoutData)
    }

    fun bind(context: Context, content: CONTENT, layoutData: Any?): BIND_DATA? {
      return binder.bind(context, content, model, layoutData)
    }

    fun unbind(context: Context, content: CONTENT, layoutData: Any?, bindData: BIND_DATA?) {
      binder.unbind(context, content, model, layoutData, bindData)
    }

    val description: String
      get() = binder.description

    companion object {
      /**
       * Create a binder with a Model and [Binder] which will bind the given Model to the content
       * type which will be provided by the RenderUnit.
       */
      @JvmStatic
      fun <MODEL, CONTENT, BIND_DATA : Any> createDelegateBinder(
          model: MODEL,
          binder: Binder<MODEL, CONTENT, BIND_DATA>
      ): DelegateBinder<MODEL, CONTENT, BIND_DATA> {
        return DelegateBinder(model, binder)
      }
    }
  }

  /**
   * Represents a single bind function. Every bind has an equivalent unbind and a shouldUpdate
   * callback
   */
  interface Binder<MODEL, CONTENT, BIND_DATA : Any> {
    /**
     * Decides of the model should be re-bound to the content. If this method returns true, then
     * unbind will be called followed by bind.
     */
    fun shouldUpdate(
        currentModel: MODEL,
        newModel: MODEL,
        currentLayoutData: Any?,
        nextLayoutData: Any?
    ): Boolean

    /**
     * Binds the model to the content and returns optional bind data that will be passed to unbind.
     */
    fun bind(context: Context, content: CONTENT, model: MODEL, layoutData: Any?): BIND_DATA?

    /** Unbinds the model from the content. */
    fun unbind(
        context: Context,
        content: CONTENT,
        model: MODEL,
        layoutData: Any?,
        bindData: BIND_DATA?
    )

    val description: String
      get() = getSectionNameForTracing(javaClass)
  }

  companion object {
    // RenderUnit's description is used for tracing, and according to:
    // https://developer.android.com/reference/android/os/Trace#beginSection(java.lang.String)
    // the max length of the tracing section name is 127
    const val MAX_DESCRIPTION_LENGTH: Int = 127
    private const val MAX_FIXED_MOUNT_BINDERS_COUNT = 64

    // Make sure a binder with the same Binder is not already defined in this RenderUnit.
    // If that's the case, remove the old binder and add the new one at the current list position
    // which is at the end.
    private fun <MOUNT_CONTENT> addBinder(
        binderTypeToBinderMap: MutableMap<Class<*>, DelegateBinder<*, in MOUNT_CONTENT, *>>,
        binders: MutableList<DelegateBinder<*, in MOUNT_CONTENT, *>>,
        binder: DelegateBinder<*, in MOUNT_CONTENT, *>
    ) {
      val prevBinder = binderTypeToBinderMap.put(binder.binder.javaClass, binder)
      if (prevBinder != null) {
        // A binder with the same type was already present and it should be removed.
        var found = false
        for (i in binders.indices.reversed()) {
          if (binders[i].binder.javaClass == binder.binder.javaClass) {
            binders.removeAt(i)
            found = true
            break
          }
        }
        check(found) { "Binder Map and Binder List out of sync!" }
      }
      binders.add(binder)
    }

    /**
     * This methods validates current and new fixed mount binders, calling shouldUpdate if needed,
     * and returning a long value with bits set to 1 for all fixed binders that need to be updated.
     */
    private fun <MOUNT_CONTENT> resolveFixedMountBindersToUpdate(
        currentFixedBinders: List<DelegateBinder<*, in MOUNT_CONTENT, *>>?,
        newFixedBinders: List<DelegateBinder<*, in MOUNT_CONTENT, *>>?,
        currentLayoutData: Any?,
        newLayoutData: Any?
    ): Long {
      if (currentFixedBinders.isNullOrEmpty() && newFixedBinders.isNullOrEmpty()) {
        return 0
      }
      var fixedMountBindersToUpdate: Long = 0
      check(currentFixedBinders?.size == newFixedBinders?.size) {
        ("Current and new fixed Mount Binders are of sync: \n" +
            "currentFixedBinders.size() = " +
            currentFixedBinders?.size +
            "\n" +
            "newFixedBinders.size() = " +
            newFixedBinders?.size)
      }
      currentFixedBinders ?: return 0
      newFixedBinders ?: return 0
      for (i in currentFixedBinders.indices) {
        val currentBinder: DelegateBinder<Any, Any, Any> =
            currentFixedBinders[i] as DelegateBinder<Any, Any, Any>
        val newBinder: DelegateBinder<Any, Any, Any> =
            newFixedBinders[i] as DelegateBinder<Any, Any, Any>
        if (newBinder.shouldUpdate(currentBinder, currentLayoutData, newLayoutData)) {
          fixedMountBindersToUpdate = fixedMountBindersToUpdate or (0x1L shl i)
        }
      }
      return fixedMountBindersToUpdate
    }

    /**
     * This methods diff current and new binders, calling shouldUpdate if needed, and returning a
     * list of binders from the "current" ones to unbind, and a list of binders from the "new" ones
     * to bind.
     */
    private fun <MOUNT_CONTENT> resolveBindersToUpdate(
        currentBinders: List<DelegateBinder<*, in MOUNT_CONTENT, *>>?,
        newBinders: List<DelegateBinder<*, in MOUNT_CONTENT, *>>?,
        currentBinderTypeToBinderMap: Map<Class<*>, DelegateBinder<*, in MOUNT_CONTENT, *>>?,
        currentLayoutData: Any?,
        newLayoutData: Any?,
        bindersToBind: MutableList<DelegateBinder<*, in MOUNT_CONTENT, *>>,
        bindersToUnbind: MutableList<DelegateBinder<*, in MOUNT_CONTENT, *>>
    ) {

      // There's nothing to unbind because there aren't any current binders, we need to bind all
      // new binders.
      if (currentBinders.isNullOrEmpty()) {
        newBinders?.let { bindersToBind.addAll(it) }
        return
      }

      // There's no new binders. All current binders have to be unbound.
      if (newBinders.isNullOrEmpty()) {
        bindersToUnbind.addAll(currentBinders)
        return
      }
      val binderToShouldUpdate: MutableMap<Class<*>, Boolean> = HashMap(newBinders.size)

      // Parse all new binders and resolve which ones are to bind.
      for (i in newBinders.indices) {
        val newBinder = newBinders[i]
        val binderClass: Class<*> = newBinder.binder.javaClass
        val currentBinder = currentBinderTypeToBinderMap?.get(binderClass)
        if (currentBinder == null) {
          // Found new binder, has to be bound.
          bindersToBind.add(newBinder)
          continue
        }
        val shouldUpdate =
            (newBinder as DelegateBinder<Any, Any, Any>).shouldUpdate(
                currentBinder as DelegateBinder<Any, Any, Any>, currentLayoutData, newLayoutData)
        // Memoize the result for the next for-loop.
        binderToShouldUpdate[binderClass] = shouldUpdate
        if (shouldUpdate) {
          bindersToBind.add(newBinder)
        }
      }

      // Parse all current binders and resolve which ones are to unbind.
      for (i in currentBinders.indices) {
        val currentBinder = currentBinders[i]
        val binderClass: Class<*> = currentBinder.binder.javaClass
        if (!binderToShouldUpdate.containsKey(binderClass) ||
            binderToShouldUpdate[binderClass] == true) {
          // Found a current binder which either is not in the new RenderUnit or shouldUpdate is
          // true, therefore we need to unbind it.
          bindersToUnbind.add(currentBinder)
        }
      }
    }

    private fun sizeOrZero(collection: Collection<*>?): Int {
      return collection?.size ?: 0
    }

    private fun sectionName(name: String): String {
      return if (name.length <= MAX_DESCRIPTION_LENGTH) {
        name
      } else name.substring(0, MAX_DESCRIPTION_LENGTH)
    }
  }

  /**
   * This is a wrapper [Exception] that appends information about the [RenderUnit] that caused the
   * exception.
   *
   * The [message] should help to reflect which was the faulty operation and provide specific
   * contextual data if possible.
   */
  class RenderUnitOperationException(renderUnit: RenderUnit<*>, message: String, cause: Throwable) :
      RuntimeException("[${renderUnit.description}] $message") {

    init {
      initCause(cause)
      stackTrace = emptyArray()
    }
  }
}
