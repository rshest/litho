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

package com.facebook.litho.config;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;

import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Nullsafe;
import com.facebook.litho.BuildConfig;
import com.facebook.litho.perfboost.LithoPerfBoosterFactory;
import java.util.Set;

/**
 * Hi there, traveller! This configuration class is not meant to be used by end-users of Litho. It
 * contains mainly flags for features that are either under active development and not ready for
 * public consumption, or for use in experiments.
 *
 * <p>These values are safe defaults and should not require manual changes.
 *
 * <p>This class hosts all the config parameters that the ComponentTree configures it self ....
 * enable and disable features ... A Component tree uses the {@link #defaultComponentsConfiguration}
 * by default but a {@link Builder} can be used to create new instances of the config to override
 * the default parameters ... The default config values can also be overridden by manually setting
 * their values in {@link #defaultBuilder}
 */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class ComponentsConfiguration {

  /**
   * Indicates whether this is an internal build. Note that the implementation of <code>BuildConfig
   * </code> that this class is compiled against may not be the one that is included in the APK.
   * See: <a
   * href="http://facebook.github.io/buck/rule/android_build_config.html">android_build_config</a>.
   */
  public static final boolean IS_INTERNAL_BUILD = BuildConfig.IS_INTERNAL_BUILD;

  /** Indicates that the incremental mount helper is required for this build. */
  public static final boolean USE_INCREMENTAL_MOUNT_HELPER =
      BuildConfig.USE_INCREMENTAL_MOUNT_HELPER;

  /** Whether we can access properties in Settings.Global for animations. */
  public static final boolean CAN_CHECK_GLOBAL_ANIMATOR_SETTINGS = (SDK_INT >= JELLY_BEAN_MR1);

  /** Whether we need to account for lack of synchronization while accessing Themes. */
  public static final boolean NEEDS_THEME_SYNCHRONIZATION = (SDK_INT <= LOLLIPOP_MR1);

  /** The default priority for threads that perform background layout calculations. */
  public static int DEFAULT_BACKGROUND_THREAD_PRIORITY = 5;

  /** The default priority for threads that perform background sections change set calculations. */
  public static final int DEFAULT_CHANGE_SET_THREAD_PRIORITY = 0;

  /**
   * Option to enabled debug mode. This will save extra data asscociated with each node and allow
   * more info about the hierarchy to be retrieved. Used to enable stetho integration. It is highly
   * discouraged to enable this in production builds. Due to how the Litho releases are distributed
   * in open source IS_INTERNAL_BUILD will always be false. It is therefore required to override
   * this value using your own application build configs. Recommended place for this is in a
   * Application subclass onCreate() method.
   */
  public static boolean isDebugModeEnabled = IS_INTERNAL_BUILD;

  /** Lightweight tracking of component class hierarchy of MountItems. */
  public static boolean isDebugHierarchyEnabled = false;

  /**
   * Populates additional metadata to find mounted components at runtime. Defaults to the presence
   * of an
   *
   * <pre>IS_TESTING</pre>
   *
   * system property at startup but can be overridden at runtime.
   */
  public static boolean isEndToEndTestRun = System.getProperty("IS_TESTING") != null;

  public static boolean isAnimationDisabled =
      "true".equals(System.getProperty("litho.animation.disabled"));

  /**
   * By default end-to-end tests will disable transitions and this flag lets to explicitly enable
   * transitions to test animation related behavior.
   */
  public static boolean forceEnableTransitionsForInstrumentationTests = false;

  public static boolean enableErrorBoundaryComponent = false;

  /**
   * If non-null, a thread pool will be used for async layouts instead of a single layout thread.
   */
  public static @Nullable LayoutThreadPoolConfiguration threadPoolConfiguration = null;

  public static boolean enableThreadTracingStacktrace = false;

  /** Sets if is reconciliation is enabled */
  public static boolean isReconciliationEnabled = true;

  public static @Nullable Boolean overrideReconciliation = null;

  public static @Nullable Boolean overrideLayoutDiffing = null;

  /**
   * Sets if layout diffing is enabled. This should be used in conjugation with
   * {@link#isReconciliationEnabled}.
   */
  public static boolean isLayoutDiffingEnabled = true;

  public static boolean runLooperPrepareForLayoutThreadFactory = true;

  public static boolean enablePrimitiveComponentsInOrion = false;

  /**
   * field.getAnnotation() has bugs which is causing java crashes in the App, in addition to this we
   * suspect this might be a reason for few other native crashes as well. Adding this flag to verify
   * if this is the root cause.
   */
  public static boolean disableGetAnnotationUsage;

  /** When enabled components which render to null will use a NullNode for reconciliation */
  public static boolean isNullNodeEnabled = false;

  public static boolean isRenderInfoDebuggingEnabled() {
    return isDebugModeEnabled;
  }

  public static @Nullable LithoPerfBoosterFactory perfBoosterFactory = null;

  /**
   * If true, the {@link #perfBoosterFactory} will be used to indicate that LayoutStateFuture thread
   * can use the perf boost
   */
  public static boolean boostPerfLayoutStateFuture;

  /**
   * Start parallel layout of visible range just before serial synchronous layouts in RecyclerBinder
   */
  public static boolean computeRangeOnSyncLayout = false;

  public static boolean shouldDisableBgFgOutputs = false;

  public static boolean shouldAddHostViewForRootComponent = false;

  /**
   * When true, IM will not stop when the LithoView's visible rect is empty, and will proceed to
   * unmount everything.
   */
  public static boolean shouldContinueIncrementalMountWhenVisibileRectIsEmpty = false;

  /** When {@code true}, disables incremental mount globally. */
  public static boolean isIncrementalMountGloballyDisabled = false;

  /** Keeps the litho node tree in the LayoutState. This will increase memory use. */
  public static boolean keepLithoNodes = false;

  /**
   * Used by LithoViews to determine whether or not to self-manage the view-port changes, rather
   * than rely on calls to notifyVisibleBoundsChanged.
   */
  public static boolean lithoViewSelfManageViewPortChanges = false;

  public static boolean emitMessageForZeroSizedTexture = false;

  public static int textureSizeWarningLimit = Integer.MAX_VALUE;

  public static int overlappingRenderingViewSizeLimit = Integer.MAX_VALUE;

  public static int partialAlphaWarningSizeThresold = Integer.MAX_VALUE;

  public static @Nullable Set<String> componentPreallocationBlocklist = null;

  /** Initialize sticky header during layout when its component tree is null */
  public static boolean initStickyHeaderInLayoutWhenComponentTreeIsNull = false;

  /**
   * If true, uses the root ComponentTree's mount content preallocation handler to perform
   * preallocation for nested trees.
   */
  public static boolean enableNestedTreePreallocation = false;

  /**
   * These params are part of an experiment to try to re-enable host recycling while avoiding native
   * RenderThread crashes we've seen when trying to reuse ComponentHosts.
   */
  public static boolean hostComponentRecyclingByWindowIsEnabled = false;

  public static boolean hostComponentRecyclingByMountStateIsEnabled = false;

  public static boolean unsafeHostComponentRecyclingIsEnabled = false;

  public static int hostComponentPoolSize = 30;

  /** When {@code true} ComponentTree records state change snapshots */
  public static boolean isTimelineEnabled = isDebugModeEnabled;

  public static @Nullable String timelineDocsLink = null;

  /**
   * When enabled split resolve and layout futures will each use dedicated thread handlers so that
   * they don't queue against each other.
   */
  public static boolean useSeparateThreadHandlersForResolveAndLayout = false;

  /**
   * When true, RenderTreeFuture will apply the interrupt immediately when calling
   * tryRegisterForResponse.
   */
  public static boolean isInterruptEarlyWithSplitFuturesEnabled = false;

  /** Return true when resolve and layout futures are split, and each uses its own thread handler */
  public static boolean isSplitResolveAndLayoutWithSplitHandlers() {
    return useSeparateThreadHandlersForResolveAndLayout;
  }

  /** Skip checking for root component and tree-props while layout */
  public static boolean isSkipRootCheckingEnabled = false;

  /** Mark sync operations as non-interruptible */
  public static boolean isSyncTaskNonInterruptibleEnabled = false;

  public static boolean shouldCompareCommonPropsInIsEquivalentTo = false;

  public static boolean shouldCompareRootCommonPropsInSingleComponentSection = false;

  public static boolean shouldDelegateContentDescriptionChangeEvent = false;

  /** This toggles whether {@Link #LayoutThreadPoolExecutor} should timeout core threads or not */
  public static boolean shouldAllowCoreThreadTimeout = false;

  public static long layoutThreadKeepAliveTimeMs = 1000;

  public static boolean crashIfExceedingStateUpdateThreshold = false;

  public static boolean enableRecyclerBinderStableId = false;

  public static boolean requestMountForPrefetchedItems = false;

  public static int recyclerBinderStrategy = 0;

  public static boolean enableMountableRecycler = false;

  public static boolean enableMountableTwoBindersRecycler = false;

  public static boolean enableMountableInNewsFeed = false;

  public static boolean enableMountableInMessenger = false;

  public static boolean enableSeparateAnimatorBinder = false;

  public static boolean enableMountableRecyclerInGroups = false;

  public static boolean enableMountableInFacecast = false;

  public static boolean enableMountableInIGDS = false;
  public static boolean enableMountableInIG4A = false;

  public static boolean crashOnWrongUseRefUsage = false;

  private static boolean sReduceMemorySpikeUserSession = false;
  private static boolean sReduceMemorySpikeDataDiffSection = false;
  private static boolean sReduceMemorySpikeGetUri = false;

  public static void setReduceMemorySpikeUserSession() {
    sReduceMemorySpikeUserSession = true;
  }

  public static boolean reduceMemorySpikeUserSession() {
    return sReduceMemorySpikeUserSession;
  }

  public static void setReduceMemorySpikeDataDiffSection() {
    sReduceMemorySpikeDataDiffSection = true;
  }

  public static boolean reduceMemorySpikeDataDiffSection() {
    return sReduceMemorySpikeDataDiffSection;
  }

  public static void setReduceMemorySpikeGetUri() {
    sReduceMemorySpikeGetUri = true;
  }

  public static boolean reduceMemorySpikeGetUri() {
    return sReduceMemorySpikeGetUri;
  }

  public static boolean enableStateUpdatesBatching = true;

  @Nullable private ResolveCancellationStrategy mResolveCancellationStrategy = null;

  @Nullable
  public ResolveCancellationStrategy getResolveCancellationStrategy() {
    return mResolveCancellationStrategy;
  }

  private boolean mIsLayoutCancellationEnabled = false;

  public boolean isLayoutCancellationEnabled() {
    return mIsLayoutCancellationEnabled;
  }

  /** Debug option to highlight interactive areas in mounted components. */
  public static boolean debugHighlightInteractiveBounds = false;

  /** Debug option to highlight mount bounds of mounted components. */
  public static boolean debugHighlightMountBounds = false;

  /** Debug option to flash component whenever it is re-rendered. */
  public static boolean debugFlashComponentOnRender = false;

  private static ComponentsConfiguration.Builder defaultBuilder = new Builder();

  private static ComponentsConfiguration defaultComponentsConfiguration = defaultBuilder.build();

  public static void setDefaultComponentsConfigurationBuilder(
      ComponentsConfiguration.Builder componentsConfigurationBuilder) {
    defaultBuilder = componentsConfigurationBuilder;
    defaultComponentsConfiguration = defaultBuilder.build();
  }

  public static ComponentsConfiguration getDefaultComponentsConfiguration() {
    return defaultComponentsConfiguration;
  }

  public static ComponentsConfiguration.Builder getDefaultComponentsConfigurationBuilder() {
    return defaultBuilder;
  }

  private final boolean mUseCancelableLayoutFutures;

  private final boolean mShouldReuseOutputs;

  private final boolean mKeepLithoNodeAndLayoutResultTreeWithReconciliation;

  private final boolean mIsLegacyRenderEnabled;

  public boolean getUseCancelableLayoutFutures() {
    return mUseCancelableLayoutFutures;
  }

  private final boolean mUsePaintAdvanceForEllipsisCalculation;

  public boolean usePaintAdvanceForEllipsisCalculation() {
    return mUsePaintAdvanceForEllipsisCalculation;
  }

  private ComponentsConfiguration(ComponentsConfiguration.Builder builder) {
    mUseCancelableLayoutFutures = builder.mUseCancelableLayoutFutures;
    mShouldReuseOutputs = builder.mShouldReuseOutputs;
    mKeepLithoNodeAndLayoutResultTreeWithReconciliation =
        builder.mKeepLithoNodeAndLayoutResultTreeWithReconciliation;
    mIsLayoutCancellationEnabled = builder.mIsLayoutCancellationEnabled;
    mResolveCancellationStrategy = builder.mResolveCancellationStrategy;
    mIsLegacyRenderEnabled = builder.mIsLegacyRenderEnabled;
    mUsePaintAdvanceForEllipsisCalculation = builder.mUsePaintAdvanceForEllipsisCalculation;
  }

  public boolean shouldReuseOutputs() {
    return mShouldReuseOutputs;
  }

  public boolean keepLithoNodeAndLayoutResultTreeWithReconciliation() {
    return mKeepLithoNodeAndLayoutResultTreeWithReconciliation;
  }

  public boolean isLegacyRenderEnabled() {
    return mIsLegacyRenderEnabled;
  }

  public static ComponentsConfiguration.Builder create() {
    return create(defaultComponentsConfiguration);
  }

  public static ComponentsConfiguration.Builder create(
      ComponentsConfiguration componentsConfiguration) {
    return new Builder()
        .useCancelableLayoutFutures(componentsConfiguration.getUseCancelableLayoutFutures());
  }

  public static class Builder {
    boolean mUsePaintAdvanceForEllipsisCalculation = false;
    boolean mUseCancelableLayoutFutures;
    boolean mShouldReuseOutputs = false;
    boolean mKeepLithoNodeAndLayoutResultTreeWithReconciliation = false;
    boolean mIsLayoutCancellationEnabled = false;
    boolean mIsLegacyRenderEnabled = false;
    @Nullable ResolveCancellationStrategy mResolveCancellationStrategy;

    protected Builder() {}

    public ComponentsConfiguration.Builder useCancelableLayoutFutures(
        boolean useCancelableLayoutFutures) {
      this.mUseCancelableLayoutFutures = useCancelableLayoutFutures;
      return this;
    }

    public ComponentsConfiguration.Builder keepLithoNodeAndLayoutResultTreeWithReconciliation(
        boolean keepLithoNodeAndLayoutResultTreeWithReconciliation) {
      this.mKeepLithoNodeAndLayoutResultTreeWithReconciliation =
          keepLithoNodeAndLayoutResultTreeWithReconciliation;
      return this;
    }

    public ComponentsConfiguration.Builder resolveCancellationStrategy(
        @Nullable ResolveCancellationStrategy strategy) {
      mResolveCancellationStrategy = strategy;
      return this;
    }

    public ComponentsConfiguration.Builder isLayoutCancellationEnabled(boolean enabled) {
      mIsLayoutCancellationEnabled = enabled;
      return this;
    }

    public ComponentsConfiguration.Builder isLegacyRenderEnabled(boolean enabled) {
      mIsLegacyRenderEnabled = enabled;
      return this;
    }

    /**
     * If enabled it will use the {@link TextSpec.getEllipsisOffsetFromPaintAdvance} as the method
     * to calculate the ellipsis target point.
     *
     * <p>This is used for an experiment that is targetting reducing ANRs by avoiding a code path
     * that is associated to a lot of ANRs.
     */
    public ComponentsConfiguration.Builder withTextPaintAdvanceEllipsisCalculation(
        boolean enabled) {
      mUsePaintAdvanceForEllipsisCalculation = enabled;
      return this;
    }

    public ComponentsConfiguration build() {
      return new ComponentsConfiguration(this);
    }
  }
}
