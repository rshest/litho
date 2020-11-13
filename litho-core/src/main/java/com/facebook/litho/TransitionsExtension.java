/*
 * Copyright (c) Facebook, Inc. and its affiliates.
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

package com.facebook.litho;

import static com.facebook.litho.LayoutOutput.getLayoutOutput;
import static com.facebook.litho.ThreadUtils.assertMainThread;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import com.facebook.litho.animation.AnimatedProperties;
import com.facebook.litho.animation.PropertyHandle;
import com.facebook.rendercore.Function;
import com.facebook.rendercore.Host;
import com.facebook.rendercore.MountItem;
import com.facebook.rendercore.RenderTreeNode;
import com.facebook.rendercore.RenderUnit;
import com.facebook.rendercore.UnmountDelegateExtension;
import com.facebook.rendercore.extensions.ExtensionState;
import com.facebook.rendercore.extensions.MountExtension;
import com.facebook.rendercore.utils.BoundsUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Extension for performing transitions. */
public class TransitionsExtension
    extends MountExtension<
        TransitionsExtensionInput, TransitionsExtension.TransitionsExtensionState>
    implements UnmountDelegateExtension<TransitionsExtension.TransitionsExtensionState> {

  private static TransitionsExtension sInstance = new TransitionsExtension();

  private TransitionsExtension() {}

  public static TransitionsExtension getInstance() {
    return sInstance;
  }

  static class TransitionsExtensionState {
    private final Map<TransitionId, OutputUnitsAffinityGroup<MountItem>> mDisappearingMountItems =
        new LinkedHashMap<>();
    private final Set<MountItem> mLockedDisappearingMountitems = new HashSet<>();
    @Deprecated private @Nullable Host mLithoView;
    private TransitionsExtensionInput mInput;
    private int mLastMountedComponentTreeId = ComponentTree.INVALID_ID;
    private TransitionManager mTransitionManager;
    private final HashSet<TransitionId> mAnimatingTransitionIds = new HashSet<>();

    private boolean mTransitionsHasBeenCollected = false;
    private @Nullable Transition mRootTransition;
    private @Nullable TransitionsExtensionInput mLastTransitionsExtensionInput;
  }

  private static class AnimationCompleteListener
      implements TransitionManager.OnAnimationCompleteListener<Function<TransitionEndEvent>> {

    private final ExtensionState<TransitionsExtensionState> mExtensionState;
    private final TransitionsExtensionState mState;

    private AnimationCompleteListener(ExtensionState<TransitionsExtensionState> extensionState) {
      mExtensionState = extensionState;
      mState = mExtensionState.getState();
    }

    @Override
    public void onAnimationComplete(TransitionId transitionId) {
      final OutputUnitsAffinityGroup<MountItem> disappearingGroup =
          mState.mDisappearingMountItems.remove(transitionId);
      if (disappearingGroup != null) {
        endUnmountDisappearingItem(mExtensionState, disappearingGroup);
      } else {
        if (!mState.mAnimatingTransitionIds.remove(transitionId)) {
          if (AnimationsDebug.ENABLED) {
            Log.e(
                AnimationsDebug.TAG,
                "Ending animation for id "
                    + transitionId
                    + " but it wasn't recorded as animating!");
          }
        }

        final OutputUnitsAffinityGroup<AnimatableItem> animatableItemGroup =
            mState.mLastTransitionsExtensionInput != null
                ? mState.mLastTransitionsExtensionInput.getAnimatableItemForTransitionId(
                    transitionId)
                : null;
        if (animatableItemGroup == null) {
          // This can happen if the component was unmounted without animation or the transitionId
          // was removed from the component.
          return;
        }

        for (int i = 0, size = animatableItemGroup.size(); i < size; i++) {
          final int position =
              mState.mLastTransitionsExtensionInput.getPositionForId(
                  animatableItemGroup.getAt(i).getId());
          updateAnimationLockCount(
              mExtensionState, mState.mLastTransitionsExtensionInput, position, false);
        }
      }
    }

    @Override
    public void onAnimationUnitComplete(
        PropertyHandle propertyHandle, Function transitionEndHandler) {
      if (transitionEndHandler != null) {
        transitionEndHandler.call(
            new TransitionEndEvent(
                propertyHandle.getTransitionId().mReference, propertyHandle.getProperty()));
      }
    }
  }

  /** @deprecated Only used for Litho's integration. Marked for removal. */
  @Deprecated
  public static void setRootHost(
      ExtensionState<TransitionsExtensionState> extensionState, Host root) {
    final TransitionsExtensionState state = extensionState.getState();
    state.mLithoView = root;
  }

  @Override
  public boolean shouldDelegateUnmount(
      ExtensionState<TransitionsExtensionState> extensionState, MountItem mountItem) {
    final TransitionsExtensionState state = extensionState.getState();
    return state.mLockedDisappearingMountitems.contains(mountItem);
  }

  @Override
  public void unmount(
      final ExtensionState<TransitionsExtensionState> extensionState,
      final MountItem mountItem,
      final Host host) {
    final LayoutOutput layoutOutput = getLayoutOutput(mountItem);
    final TransitionId transitionId = layoutOutput.getTransitionId();
    final TransitionsExtensionState state = extensionState.getState();
    final OutputUnitsAffinityGroup<MountItem> group =
        state.mDisappearingMountItems.get(transitionId);
    if (group != null) {
      final boolean isRoot =
          group.get(LayoutStateOutputIdCalculator.getTypeFromId(layoutOutput.getId())) != null;
      // We only start unmount disappearing item on the root of the disappearing animation. The rest
      // will be unmounted after the animation finishes.
      if (isRoot) {
        ((ComponentHost) host).startUnmountDisappearingItem(mountItem);
      }
    }
  }

  @Override
  protected TransitionsExtensionState createState() {
    return new TransitionsExtensionState();
  }

  @Override
  public void beforeMount(
      ExtensionState<TransitionsExtensionState> extensionState,
      TransitionsExtensionInput input,
      Rect localVisibleRect) {
    final TransitionsExtensionState state = extensionState.getState();
    state.mInput = input;

    if (input.getComponentTreeId() != state.mLastMountedComponentTreeId) {
      state.mLastTransitionsExtensionInput = null;
    }

    updateTransitions(extensionState, input, ((LithoView) state.mLithoView).getComponentTree());
    extractDisappearingItems(extensionState, input);
  }

  @Override
  public void afterMount(ExtensionState<TransitionsExtensionState> extensionState) {
    final TransitionsExtensionState state = extensionState.getState();
    maybeUpdateAnimatingMountContent(extensionState);

    if (shouldAnimateTransitions(state, state.mInput) && hasTransitionsToAnimate(state)) {
      state.mTransitionManager.runTransitions();
    }

    state.mInput.setNeedsToRerunTransitions(false);
    state.mLastTransitionsExtensionInput = state.mInput;
    state.mTransitionsHasBeenCollected = false;
    state.mLastMountedComponentTreeId = state.mInput.getComponentTreeId();
  }

  @Override
  public void onUnmount(ExtensionState<TransitionsExtensionState> extensionState) {
    extensionState.releaseAllAcquiredReferences();
    clearLastMountedTreeId(extensionState);
  }

  @Override
  public void onUnbind(ExtensionState<TransitionsExtensionState> extensionState) {
    extensionState.releaseAllAcquiredReferences();
  }

  public void clearLastMountedTreeId(ExtensionState<TransitionsExtensionState> extensionState) {
    final TransitionsExtensionState state = extensionState.getState();
    state.mLastMountedComponentTreeId = ComponentTree.INVALID_ID;
  }

  @Override
  public void onUnbindItem(
      final ExtensionState<TransitionsExtensionState> extensionState,
      final RenderUnit<?> renderUnit,
      final Object content,
      final @Nullable Object layoutData) {
    if (renderUnit instanceof LithoRenderUnit) {
      final LayoutOutput output = ((LithoRenderUnit) renderUnit).output;
      final TransitionsExtensionState state = extensionState.getState();

      // If this item is a host and contains disappearing items, we need to remove them.
      if (content instanceof ComponentHost) {
        removeDisappearingMountContentFromComponentHost(extensionState, (ComponentHost) content);
      }
      if (output.getTransitionId() != null) {
        final @OutputUnitType int type =
            LayoutStateOutputIdCalculator.getTypeFromId(output.getId());
        maybeRemoveAnimatingMountContent(state, output.getTransitionId(), type);
      }
    }
  }

  @Override
  public void onMountItem(
      final ExtensionState<TransitionsExtensionState> extensionState,
      final RenderUnit<?> renderUnit,
      final Object content,
      final @Nullable Object layoutData) {
    if (renderUnit instanceof LithoRenderUnit) {
      final LayoutOutput output = ((LithoRenderUnit) renderUnit).output;

      if (extensionState.ownsReference(output.getId())
          && output.getComponent().hasChildLithoViews()) {
        final View view = (View) content;
        MountUtils.ensureAllLithoViewChildrenAreMounted(view);
      }
    }
  }

  /**
   * Creates and updates transitions for a new TransitionsExtensionInput. The steps are as follows:
   *
   * <p>1. Disappearing items: Update disappearing mount items that are no longer disappearing (e.g.
   * because they came back). This means canceling the animation and cleaning up the corresponding
   * ComponentHost.
   *
   * <p>2. New transitions: Use the transition manager to create new animations.
   *
   * <p>3. Update locked indices: Based on running/new animations, there are some mount items we
   * want to make sure are not unmounted due to incremental mount and being outside of visibility
   * bounds.
   */
  private static void updateTransitions(
      ExtensionState<TransitionsExtensionState> extensionState,
      TransitionsExtensionInput input,
      ComponentTree componentTree) {
    final TransitionsExtensionState state = extensionState.getState();
    final boolean isTracing = ComponentsSystrace.isTracing();
    if (isTracing) {
      String logTag = componentTree.getContext().getLogTag();
      if (logTag == null) {
        ComponentsSystrace.beginSection("MountState.updateTransitions");
      } else {
        ComponentsSystrace.beginSection("MountState.updateTransitions:" + logTag);
      }
    }

    try {
      // If this is a new component tree but isn't the first time it's been mounted, then we
      // shouldn't
      // do any transition animations for changed mount content as it's just being remounted on a
      // new LithoView.
      final int componentTreeId = componentTree.mId;
      if (state.mLastMountedComponentTreeId != componentTreeId) {
        resetAnimationState(extensionState);
        if (!state.mInput.needsToRerunTransitions()) {
          // Don't re-trigger appear animations were scrolled back onto the screen
          return;
        }
      }

      if (!state.mDisappearingMountItems.isEmpty()) {
        updateDisappearingMountItems(extensionState, input);
      }

      if (shouldAnimateTransitions(state, input)) {
        collectAllTransitions(extensionState, input, componentTree);
        if (hasTransitionsToAnimate(state)) {
          createNewTransitions(extensionState, input, state.mRootTransition);
        }
      }

      if (state.mTransitionManager != null) {
        state.mTransitionManager.finishUndeclaredTransitions();
      }
      extensionState.releaseAllAcquiredReferences();
      if (!state.mAnimatingTransitionIds.isEmpty()) {
        regenerateAnimationLockedIndices(extensionState, input);
      }
    } finally {
      if (isTracing) {
        ComponentsSystrace.endSection();
      }
    }
  }

  private static void updateDisappearingMountItems(
      final ExtensionState<TransitionsExtensionState> extensionState,
      TransitionsExtensionInput input) {
    final TransitionsExtensionState state = extensionState.getState();

    final Map<TransitionId, ?> nextMountedTransitionIds = input.getTransitionIdMapping();
    for (TransitionId transitionId : nextMountedTransitionIds.keySet()) {
      final OutputUnitsAffinityGroup<MountItem> disappearingItem =
          state.mDisappearingMountItems.remove(transitionId);
      if (disappearingItem != null) {
        endUnmountDisappearingItem(extensionState, disappearingItem);
      }
    }
  }

  private static void resetAnimationState(
      final ExtensionState<TransitionsExtensionState> extensionState) {
    final TransitionsExtensionState state = extensionState.getState();
    if (state.mTransitionManager == null) {
      return;
    }

    for (OutputUnitsAffinityGroup<MountItem> group : state.mDisappearingMountItems.values()) {
      endUnmountDisappearingItem(extensionState, group);
    }

    extensionState.releaseAllAcquiredReferences();
    state.mDisappearingMountItems.clear();
    state.mLockedDisappearingMountitems.clear();
    state.mAnimatingTransitionIds.clear();
    state.mTransitionManager.reset();
  }

  private static void regenerateAnimationLockedIndices(
      final ExtensionState<TransitionsExtensionState> extensionState,
      TransitionsExtensionInput input) {
    final TransitionsExtensionState state = extensionState.getState();
    final Map<TransitionId, OutputUnitsAffinityGroup<AnimatableItem>> transitionMapping =
        input.getTransitionIdMapping();
    if (transitionMapping != null) {
      for (Map.Entry<TransitionId, OutputUnitsAffinityGroup<AnimatableItem>> transition :
          transitionMapping.entrySet()) {
        if (!state.mAnimatingTransitionIds.contains(transition.getKey())) {
          continue;
        }

        final OutputUnitsAffinityGroup<AnimatableItem> group = transition.getValue();
        for (int j = 0, sz = group.size(); j < sz; j++) {
          final int position = input.getPositionForId(group.getAt(j).getId());
          updateAnimationLockCount(extensionState, input, position, true);
        }
      }
    }
  }

  /** @return whether we should animate transitions. */
  private static boolean shouldAnimateTransitions(
      final TransitionsExtensionState state, TransitionsExtensionInput input) {
    return (state.mLastMountedComponentTreeId == input.getComponentTreeId()
        || state.mInput.needsToRerunTransitions());
  }

  /** @return whether we have any transitions to animate for the current mount */
  private static boolean hasTransitionsToAnimate(final TransitionsExtensionState state) {
    return state.mRootTransition != null;
  }

  /**
   * Collect transitions from layout time, mount time and from state updates.
   *
   * @param input provides transitions information for the current mount.
   */
  static void collectAllTransitions(
      final ExtensionState<TransitionsExtensionState> extensionState,
      TransitionsExtensionInput input,
      ComponentTree componentTree) {
    final TransitionsExtensionState state = extensionState.getState();
    assertMainThread();
    if (state.mTransitionsHasBeenCollected) {
      return;
    }

    final ArrayList<Transition> allTransitions = new ArrayList<>();

    if (input.getTransitions() != null) {
      allTransitions.addAll(input.getTransitions());
    }
    componentTree.applyPreviousRenderData(input.getComponentsNeedingPreviousRenderData());
    collectMountTimeTransitions(input, allTransitions);
    componentTree.consumeStateUpdateTransitions(allTransitions, input.getRootComponentName());

    Transition.RootBoundsTransition rootWidthTransition = new Transition.RootBoundsTransition();
    Transition.RootBoundsTransition rootHeightTransition = new Transition.RootBoundsTransition();

    final TransitionId rootTransitionId = input.getRootTransitionId();

    if (rootTransitionId != null) {
      for (int i = 0, size = allTransitions.size(); i < size; i++) {
        final Transition transition = allTransitions.get(i);
        if (transition == null) {
          throw new IllegalStateException(
              "NULL_TRANSITION when collecting root bounds anim. Root: "
                  + input.getRootComponentName()
                  + ", root TransitionId: "
                  + rootTransitionId);
        }
        TransitionUtils.collectRootBoundsTransitions(
            rootTransitionId, transition, AnimatedProperties.WIDTH, rootWidthTransition);

        TransitionUtils.collectRootBoundsTransitions(
            rootTransitionId, transition, AnimatedProperties.HEIGHT, rootHeightTransition);
      }
    }

    rootWidthTransition = rootWidthTransition.hasTransition ? rootWidthTransition : null;
    rootHeightTransition = rootHeightTransition.hasTransition ? rootHeightTransition : null;

    componentTree.setRootWidthAnimation(rootWidthTransition);
    componentTree.setRootHeightAnimation(rootHeightTransition);

    state.mRootTransition = TransitionManager.getRootTransition(allTransitions);
    state.mTransitionsHasBeenCollected = true;
  }

  private static @Nullable void collectMountTimeTransitions(
      TransitionsExtensionInput input, List<Transition> outList) {
    final List<Component> componentsNeedingPreviousRenderData =
        input.getComponentsNeedingPreviousRenderData();

    if (componentsNeedingPreviousRenderData == null) {
      return;
    }

    for (int i = 0, size = componentsNeedingPreviousRenderData.size(); i < size; i++) {
      final Component component = componentsNeedingPreviousRenderData.get(i);
      // TOOD: will wait until we move the TransitionsExtension out of Litho.
      final Transition transition =
          component.createTransition(component.getScopedContext(null, null));
      if (transition != null) {
        TransitionUtils.addTransitions(transition, outList, input.getRootComponentName());
      }
    }
  }

  private static void prepareTransitionManager(
      final ExtensionState<TransitionsExtensionState> extensionState) {
    final TransitionsExtensionState state = extensionState.getState();
    if (state.mTransitionManager == null) {
      state.mTransitionManager =
          new TransitionManager(new AnimationCompleteListener(extensionState));
    }
  }

  private static void createNewTransitions(
      final ExtensionState<TransitionsExtensionState> extensionState,
      TransitionsExtensionInput input,
      Transition rootTransition) {
    final TransitionsExtensionState state = extensionState.getState();
    prepareTransitionManager(extensionState);

    Map<TransitionId, OutputUnitsAffinityGroup<AnimatableItem>> lastTransitions =
        state.mLastTransitionsExtensionInput == null
            ? null
            : state.mLastTransitionsExtensionInput.getTransitionIdMapping();
    state.mTransitionManager.setupTransitions(
        lastTransitions, input.getTransitionIdMapping(), rootTransition);

    final Map<TransitionId, ?> nextTransitionIds = input.getTransitionIdMapping();
    for (TransitionId transitionId : nextTransitionIds.keySet()) {
      if (state.mTransitionManager.isAnimating(transitionId)) {
        state.mAnimatingTransitionIds.add(transitionId);
      }
    }
  }

  /** Determine whether to apply disappear animation to the given {@link MountItem} */
  private static boolean isItemDisappearing(
      final TransitionsExtensionState state, TransitionsExtensionInput input, int index) {
    if (!shouldAnimateTransitions(state, input) || !hasTransitionsToAnimate(state)) {
      return false;
    }

    if (state.mTransitionManager == null || state.mLastTransitionsExtensionInput == null) {
      return false;
    }

    final AnimatableItem animatableItem =
        state.mLastTransitionsExtensionInput.getAnimatableItem(
            state
                .mLastTransitionsExtensionInput
                .getMountableOutputAt(index)
                .getRenderUnit()
                .getId());

    final TransitionId transitionId = animatableItem.getTransitionId();
    if (transitionId == null) {
      return false;
    }

    return state.mTransitionManager.isDisappearing(transitionId);
  }

  /**
   * This is where we go through the new layout state and compare it to the previous one. If we find
   * we do a couple of things:
   *
   * <p>- Loop trough the disappearing tree making sure it is mounted (we mounted if it's not).
   *
   * <p>- Add all the items to a set to be able to hook the unmount delegate.
   *
   * <p>- Move the disappearing mount item to the root host.
   *
   * <p>- Finally map the disappearing mount item to the transition id
   */
  private static void extractDisappearingItems(
      ExtensionState<TransitionsExtensionState> extensionState,
      TransitionsExtensionInput newTransitionsExtensionInput) {
    int mountItemCount = getMountTarget(extensionState).getRenderUnitCount();
    final TransitionsExtensionState state = extensionState.getState();
    if (state.mLastTransitionsExtensionInput == null || mountItemCount == 0) {
      return;
    }

    for (int i = 1; i < mountItemCount; i++) {
      if (isItemDisappearing(state, newTransitionsExtensionInput, i)) {
        final int lastDescendantIndex =
            findLastDescendantIndex(state.mLastTransitionsExtensionInput, i);
        // Go though disappearing subtree. Mount anything that's not mounted (without acquiring
        // reference).
        for (int j = i; j <= lastDescendantIndex; j++) {
          if (getMountTarget(extensionState).getMountItemAt(j) == null) {
            // We need to release any mount reference to this because we need to force mount here.
            if (extensionState.ownsReference(
                state.mLastTransitionsExtensionInput.getMountableOutputAt(j))) {
              extensionState.releaseMountReference(
                  state.mLastTransitionsExtensionInput.getMountableOutputAt(j), false);
            }
            extensionState.acquireMountReference(
                state.mLastTransitionsExtensionInput.getMountableOutputAt(j), true);
            // Here we have to release the ref count without mounting.
            extensionState.releaseMountReference(
                state.mLastTransitionsExtensionInput.getMountableOutputAt(j), false);
          }
          state.mLockedDisappearingMountitems.add(getMountTarget(extensionState).getMountItemAt(j));
        }

        // Reference to the root of the disappearing subtree
        final MountItem disappearingItem = getMountTarget(extensionState).getMountItemAt(i);

        if (disappearingItem == null) {
          throw new IllegalStateException(
              "The root of the disappearing subtree should not be null,"
                  + " acquireMountReference on this index should be called before this. Index: "
                  + i);
        }

        // Moving item to the root if needed.
        remountHostToRootIfNeeded(extensionState, i, disappearingItem);

        mapDisappearingItemWithTransitionId(state, disappearingItem);

        final long id = disappearingItem.getRenderTreeNode().getRenderUnit().getId();
        getMountTarget(extensionState).notifyUnmount(id);

        i = lastDescendantIndex;
      }
    }
  }

  private static void unmountDisappearingItem(
      final ExtensionState<TransitionsExtensionState> extensionState,
      final MountItem mountItem,
      final boolean isRoot) {
    final TransitionsExtensionState state = extensionState.getState();
    state.mLockedDisappearingMountitems.remove(mountItem);
    final Object content = mountItem.getContent();
    if ((content instanceof ComponentHost) && !(content instanceof LithoView)) {
      final Host contentHost = (Host) content;
      // Unmount descendant items in reverse order.
      for (int j = contentHost.getMountItemCount() - 1; j >= 0; j--) {
        unmountDisappearingItem(extensionState, contentHost.getMountItemAt(j), false);
      }

      if (contentHost.getMountItemCount() > 0) {
        throw new IllegalStateException(
            "Recursively unmounting items from a Host, left"
                + " some items behind, this should never happen.");
      }
    }

    final ComponentHost host = (ComponentHost) mountItem.getHost();
    if (host == null) {
      throw new IllegalStateException("Disappearing mountItem has no host, can not be unmounted.");
    }
    if (isRoot) {
      host.unmountDisappearingItem(mountItem);
    } else {
      host.unmount(mountItem);
    }

    getMountTarget(extensionState).unbindMountItem(mountItem);
  }

  private static void endUnmountDisappearingItem(
      ExtensionState<TransitionsExtensionState> extensionState,
      OutputUnitsAffinityGroup<MountItem> group) {
    maybeRemoveAnimatingMountContent(
        extensionState.getState(),
        getLayoutOutput(group.getMostSignificantUnit()).getTransitionId());

    for (int i = 0, size = group.size(); i < size; i++) {
      unmountDisappearingItem(extensionState, group.getAt(i), true);
    }
  }

  private static void mapDisappearingItemWithTransitionId(
      TransitionsExtensionState state, MountItem item) {
    final AnimatableItem animatableItem =
        state.mLastTransitionsExtensionInput.getAnimatableItem(
            item.getRenderTreeNode().getRenderUnit().getId());
    final TransitionId transitionId = animatableItem.getTransitionId();
    OutputUnitsAffinityGroup<MountItem> disappearingGroup =
        state.mDisappearingMountItems.get(transitionId);
    if (disappearingGroup == null) {
      disappearingGroup = new OutputUnitsAffinityGroup<>();
      state.mDisappearingMountItems.put(transitionId, disappearingGroup);
    }
    final @OutputUnitType int type = animatableItem.getOutputType();
    disappearingGroup.add(type, item);
  }

  private static void remountHostToRootIfNeeded(
      ExtensionState<TransitionsExtensionState> extensionState, int index, MountItem mountItem) {
    final Host rootHost = getMountTarget(extensionState).getRootItem().getHost();
    final Host originalHost = mountItem.getHost();
    if (originalHost == null) {
      throw new IllegalStateException(
          "Disappearing item host should never be null. Index: " + index);
    }

    if (rootHost == originalHost) {
      // Already mounted to the root
      return;
    }

    final Object content = mountItem.getContent();
    if (content == null) {
      throw new IllegalStateException(
          "Disappearing item content should never be null. Index: " + index);
    }

    // Before unmounting item get its position inside the root
    int left = 0;
    int top = 0;
    int right;
    int bottom;
    // Get left/top position of the item's host first
    Host host = originalHost;
    while (host != rootHost) {
      left += host.getLeft();
      top += host.getTop();
      host = (Host) host.getParent();
    }

    if (content instanceof View) {
      final View view = (View) content;
      left += view.getLeft();
      top += view.getTop();
      right = left + view.getWidth();
      bottom = top + view.getHeight();
    } else {
      final Rect bounds = ((Drawable) content).getBounds();
      left += bounds.left;
      right = left + bounds.width();
      top += bounds.top;
      bottom = top + bounds.height();
    }

    // Unmount from the current host
    originalHost.unmount(mountItem);

    // Apply new bounds to the content as it will be mounted in the root now
    BoundsUtils.applyBoundsToMountContent(new Rect(left, top, right, bottom), null, content, false);

    // Mount to the root
    rootHost.mount(index, mountItem);

    // Set new host to the MountItem
    mountItem.setHost(rootHost);
  }

  private static void maybeUpdateAnimatingMountContent(
      ExtensionState<TransitionsExtensionState> extensionState) {
    final TransitionsExtensionState state = extensionState.getState();
    if (state.mTransitionManager == null) {
      return;
    }

    final boolean isTracing = ComponentsSystrace.isTracing();
    if (isTracing) {
      ComponentsSystrace.beginSection("updateAnimatingMountContent");
    }

    // Group mount content (represents current LayoutStates only) into groups and pass it to the
    // TransitionManager
    final Map<TransitionId, OutputUnitsAffinityGroup<Object>> animatingContent =
        new LinkedHashMap<>(state.mAnimatingTransitionIds.size());

    for (int i = 0, size = getMountTarget(extensionState).getRenderUnitCount(); i < size; i++) {
      final MountItem mountItem = getMountTarget(extensionState).getMountItemAt(i);
      if (mountItem == null) {
        continue;
      }
      final AnimatableItem animatableItem =
          state.mInput.getAnimatableItem(mountItem.getRenderTreeNode().getRenderUnit().getId());

      if (animatableItem.getTransitionId() == null) {
        continue;
      }
      final @OutputUnitType int type = animatableItem.getOutputType();
      OutputUnitsAffinityGroup<Object> group =
          animatingContent.get(animatableItem.getTransitionId());
      if (group == null) {
        group = new OutputUnitsAffinityGroup<>();
        animatingContent.put(animatableItem.getTransitionId(), group);
      }
      group.replace(type, mountItem.getContent());
    }
    for (Map.Entry<TransitionId, OutputUnitsAffinityGroup<Object>> content :
        animatingContent.entrySet()) {
      state.mTransitionManager.setMountContent(content.getKey(), content.getValue());
    }

    // Retrieve mount content from disappearing mount items and pass it to the TransitionManager
    for (Map.Entry<TransitionId, OutputUnitsAffinityGroup<MountItem>> entry :
        state.mDisappearingMountItems.entrySet()) {
      final OutputUnitsAffinityGroup<MountItem> mountItemsGroup = entry.getValue();
      final OutputUnitsAffinityGroup<Object> mountContentGroup = new OutputUnitsAffinityGroup<>();
      for (int j = 0, sz = mountItemsGroup.size(); j < sz; j++) {
        final @OutputUnitType int type = mountItemsGroup.typeAt(j);
        final MountItem mountItem = mountItemsGroup.getAt(j);
        mountContentGroup.add(type, mountItem.getContent());
      }
      state.mTransitionManager.setMountContent(entry.getKey(), mountContentGroup);
    }

    if (isTracing) {
      ComponentsSystrace.endSection();
    }
  }

  // TODO: T68620328 Make private after test is done
  public static void removeDisappearingMountContentFromComponentHost(
      final ExtensionState<TransitionsExtensionState> extensionState, ComponentHost componentHost) {
    final TransitionsExtensionState state = extensionState.getState();
    List<TransitionId> ids = componentHost.getDisappearingItemTransitionIds();
    if (ids != null) {
      for (int i = 0, size = ids.size(); i < size; i++) {
        state.mTransitionManager.setMountContent(ids.get(i), null);
      }
    }
  }

  private static void maybeRemoveAnimatingMountContent(
      final TransitionsExtensionState state, @Nullable TransitionId transitionId) {
    if (state.mTransitionManager == null || transitionId == null) {
      return;
    }

    state.mTransitionManager.setMountContent(transitionId, null);
  }

  private static void maybeRemoveAnimatingMountContent(
      final TransitionsExtensionState state, TransitionId transitionId, @OutputUnitType int type) {
    if (state.mTransitionManager == null || transitionId == null) {
      return;
    }

    state.mTransitionManager.removeMountContent(transitionId, type);
  }

  /**
   * Update the animation locked count for all children and each parent of the animating item. Mount
   * items that have a lock count > 0 will not be unmounted during incremental mount.
   */
  private static void updateAnimationLockCount(
      final ExtensionState<TransitionsExtensionState> extensionState,
      final TransitionsExtensionInput input,
      final int index,
      final boolean increment) {
    // Update children
    final int lastDescendantIndex = findLastDescendantIndex(input, index);
    for (int i = index; i <= lastDescendantIndex; i++) {
      final RenderTreeNode renderTreeNode = input.getMountableOutputAt(i);
      if (increment) {
        if (!extensionState.ownsReference(renderTreeNode)) {
          extensionState.acquireMountReference(renderTreeNode, false);
        }
      } else {
        if (extensionState.ownsReference(renderTreeNode)) {
          extensionState.releaseMountReference(renderTreeNode, false);
        }
      }
    }

    // Update parents
    RenderTreeNode parentRenderTreeNode = input.getMountableOutputAt(index).getParent();
    while (parentRenderTreeNode != null && parentRenderTreeNode.getParent() != null) {
      if (increment) {
        // We use the position as 0 as we are not mounting it, just acquiring reference.
        if (!extensionState.ownsReference(parentRenderTreeNode)) {
          extensionState.acquireMountReference(parentRenderTreeNode, false);
        }
        if (!extensionState.ownsReference(parentRenderTreeNode)) {
          extensionState.acquireMountReference(parentRenderTreeNode, false);
        }
      } else {
        if (extensionState.ownsReference(parentRenderTreeNode)) {
          extensionState.releaseMountReference(parentRenderTreeNode, false);
        }
      }
      parentRenderTreeNode = parentRenderTreeNode.getParent();
    }
  }

  private static int findLastDescendantIndex(TransitionsExtensionInput input, int index) {
    final RenderTreeNode rootRenderTreeNode = input.getMountableOutputAt(index);
    for (int i = index + 1, size = input.getMountableOutputCount(); i < size; i++) {
      RenderTreeNode parentRenderTreeNode = input.getMountableOutputAt(i).getParent();
      // Walk up the parents looking for the host's id: if we find it, it's a descendant. If we
      // reach the root, then it's not a descendant and we can stop.
      while (parentRenderTreeNode != rootRenderTreeNode) {
        if (parentRenderTreeNode == null || parentRenderTreeNode.getParent() == null) {
          return i - 1;
        }

        parentRenderTreeNode = parentRenderTreeNode.getParent();
      }
    }

    return input.getMountableOutputCount() - 1;
  }
}
