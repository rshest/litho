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

package com.facebook.litho;

import static androidx.core.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO;
import static androidx.core.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO;
import static androidx.core.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.facebook.litho.LithoMountData.getMountData;
import static com.facebook.litho.LithoMountData.isViewClickable;
import static com.facebook.litho.LithoMountData.isViewEnabled;
import static com.facebook.litho.LithoMountData.isViewFocusable;
import static com.facebook.litho.LithoMountData.isViewLongClickable;
import static com.facebook.litho.LithoMountData.isViewSelected;
import static com.facebook.litho.LithoRenderUnit.getRenderUnit;
import static org.assertj.core.api.Assertions.assertThat;

import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.inlinelayoutspec.InlineLayoutSpec;
import com.facebook.litho.testing.testrunner.LithoTestRunner;
import com.facebook.litho.widget.SimpleMountSpecTester;
import com.facebook.rendercore.MountItem;
import com.facebook.rendercore.RenderTreeNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Tests {@link MountItem} */
@RunWith(LithoTestRunner.class)
public class MountItemTest {
  private MountItem mMountItem;
  private Component mComponent;
  private ComponentHost mComponentHost;
  private Object mContent;
  private CharSequence mContentDescription;
  private Object mViewTag;
  private SparseArray<Object> mViewTags;
  private EventHandler mClickHandler;
  private EventHandler mLongClickHandler;
  private EventHandler mFocusChangeHandler;
  private EventHandler mTouchHandler;
  private EventHandler mDispatchPopulateAccessibilityEventHandler;
  private int mFlags;
  private ComponentContext mContext;
  private NodeInfo mNodeInfo;

  @Before
  public void setup() throws Exception {
    mContext = new ComponentContext(getApplicationContext());

    mComponent =
        new InlineLayoutSpec() {
          @Override
          protected Component onCreateLayout(ComponentContext c) {
            return SimpleMountSpecTester.create(c).build();
          }
        };
    mComponentHost = new ComponentHost(getApplicationContext());
    mContent = new View(getApplicationContext());
    mContentDescription = "contentDescription";
    mViewTag = "tag";
    mViewTags = new SparseArray<>();
    mClickHandler = new EventHandler(mComponent, 5);
    mLongClickHandler = new EventHandler(mComponent, 3);
    mFocusChangeHandler = new EventHandler(mComponent, 9);
    mTouchHandler = new EventHandler(mComponent, 1);
    mDispatchPopulateAccessibilityEventHandler = new EventHandler(mComponent, 7);
    mFlags = 114;

    mNodeInfo = new NodeInfo();
    mNodeInfo.setContentDescription(mContentDescription);
    mNodeInfo.setClickHandler(mClickHandler);
    mNodeInfo.setLongClickHandler(mLongClickHandler);
    mNodeInfo.setFocusChangeHandler(mFocusChangeHandler);
    mNodeInfo.setTouchHandler(mTouchHandler);
    mNodeInfo.setViewTag(mViewTag);
    mNodeInfo.setViewTags(mViewTags);
    mMountItem = create(mContent);
  }

  MountItem create(Object content) {
    return MountItemTestHelper.create(
        mComponent,
        mComponentHost,
        content,
        mNodeInfo,
        null,
        mFlags,
        IMPORTANT_FOR_ACCESSIBILITY_YES);
  }

  @Test
  public void testIsBound() {
    mMountItem.setIsBound(true);
    assertThat(mMountItem.isBound()).isTrue();

    mMountItem.setIsBound(false);
    assertThat(mMountItem.isBound()).isFalse();
  }

  @Test
  public void testGetters() {
    assertThat(getRenderUnit(mMountItem).getComponent()).isSameAs((Component) mComponent);
    assertThat(mMountItem.getHost()).isSameAs(mComponentHost);
    assertThat(mMountItem.getContent()).isSameAs(mContent);
    assertThat(getRenderUnit(mMountItem).getNodeInfo().getContentDescription())
        .isSameAs(mContentDescription);
    assertThat(getRenderUnit(mMountItem).getNodeInfo().getClickHandler()).isSameAs(mClickHandler);
    assertThat(getRenderUnit(mMountItem).getNodeInfo().getFocusChangeHandler())
        .isSameAs(mFocusChangeHandler);
    assertThat(getRenderUnit(mMountItem).getNodeInfo().getTouchHandler()).isSameAs(mTouchHandler);
    assertThat(getRenderUnit(mMountItem).getFlags()).isEqualTo(mFlags);
    assertThat(getRenderUnit(mMountItem).getImportantForAccessibility())
        .isEqualTo(IMPORTANT_FOR_ACCESSIBILITY_YES);
  }

  @Test
  public void testFlags() {
    mFlags =
        LithoRenderUnit.LAYOUT_FLAG_DUPLICATE_PARENT_STATE
            | LithoRenderUnit.LAYOUT_FLAG_DISABLE_TOUCHABLE;

    mMountItem = create(mContent);

    assertThat(LithoRenderUnit.isDuplicateParentState(getRenderUnit(mMountItem).getFlags()))
        .isTrue();
    assertThat(LithoRenderUnit.isTouchableDisabled(getRenderUnit(mMountItem).getFlags())).isTrue();

    mFlags = 0;
    mMountItem = create(mContent);

    assertThat(LithoRenderUnit.isDuplicateParentState(getRenderUnit(mMountItem).getFlags()))
        .isFalse();
    assertThat(LithoRenderUnit.isTouchableDisabled(getRenderUnit(mMountItem).getFlags())).isFalse();
  }

  @Test
  public void testViewFlags() {
    View view = new View(getApplicationContext());
    view.setClickable(true);
    view.setEnabled(true);
    view.setLongClickable(true);
    view.setFocusable(false);
    view.setSelected(false);

    mMountItem = create(view);

    assertThat(isViewClickable(getMountData(mMountItem).getDefaultAttributeValuesFlags())).isTrue();
    assertThat(isViewEnabled(getMountData(mMountItem).getDefaultAttributeValuesFlags())).isTrue();
    assertThat(isViewLongClickable(getMountData(mMountItem).getDefaultAttributeValuesFlags()))
        .isTrue();
    assertThat(isViewFocusable(getMountData(mMountItem).getDefaultAttributeValuesFlags()))
        .isFalse();
    assertThat(isViewSelected(getMountData(mMountItem).getDefaultAttributeValuesFlags())).isFalse();

    view.setClickable(false);
    view.setEnabled(false);
    view.setLongClickable(false);
    view.setFocusable(true);
    view.setSelected(true);

    mMountItem = create(view);

    assertThat(isViewClickable(getMountData(mMountItem).getDefaultAttributeValuesFlags()))
        .isFalse();
    assertThat(isViewEnabled(getMountData(mMountItem).getDefaultAttributeValuesFlags())).isFalse();
    assertThat(isViewLongClickable(getMountData(mMountItem).getDefaultAttributeValuesFlags()))
        .isFalse();
    assertThat(isViewFocusable(getMountData(mMountItem).getDefaultAttributeValuesFlags())).isTrue();
    assertThat(isViewSelected(getMountData(mMountItem).getDefaultAttributeValuesFlags())).isTrue();
  }

  @Test
  public void testIsAccessibleWithNullComponent() {
    final MountItem mountItem = create(mContent);

    assertThat(getRenderUnit(mountItem).isAccessible()).isFalse();
  }

  @Test
  public void testIsAccessibleWithAccessibleComponent() {
    final MountItem mountItem =
        MountItemTestHelper.create(
            TestDrawableComponent.create(mContext, true, true, true /* implementsAccessibility */)
                .build(),
            mComponentHost,
            mContent,
            mNodeInfo,
            null,
            mFlags,
            IMPORTANT_FOR_ACCESSIBILITY_AUTO);

    assertThat(getRenderUnit(mountItem).isAccessible()).isTrue();
  }

  @Test
  public void testIsAccessibleWithDisabledAccessibleComponent() {
    final MountItem mountItem =
        MountItemTestHelper.create(
            TestDrawableComponent.create(mContext, true, true, true /* implementsAccessibility */)
                .build(),
            mComponentHost,
            mContent,
            mNodeInfo,
            null,
            mFlags,
            IMPORTANT_FOR_ACCESSIBILITY_NO);

    assertThat(getRenderUnit(mountItem).isAccessible()).isFalse();
  }

  @Test
  public void testIsAccessibleWithAccessibilityEventHandler() {
    final MountItem mountItem =
        MountItemTestHelper.create(
            TestDrawableComponent.create(mContext, true, true, true /* implementsAccessibility */)
                .build(),
            mComponentHost,
            mContent,
            mNodeInfo,
            null,
            mFlags,
            IMPORTANT_FOR_ACCESSIBILITY_AUTO);

    assertThat(getRenderUnit(mountItem).isAccessible()).isTrue();
  }

  @Test
  public void testIsAccessibleWithNonAccessibleComponent() {
    assertThat(getRenderUnit(mMountItem).isAccessible()).isFalse();
  }

  @Test
  public void testUpdateDoesntChangeFlags() {
    LithoRenderUnit unit =
        MountSpecLithoRenderUnit.create(
            0, mComponent, mContext, mNodeInfo, null, 0, 0, LithoRenderUnit.STATE_UNKNOWN);
    RenderTreeNode node = RenderTreeNodeUtils.create(unit, new Rect(0, 0, 0, 0), null, null);

    View view = new View(getApplicationContext());

    final MountItem mountItem = new MountItem(node, mComponentHost, view);
    mountItem.setMountData(new LithoMountData(view));

    assertThat(isViewClickable(getMountData(mountItem).getDefaultAttributeValuesFlags())).isFalse();

    view.setClickable(true);

    mountItem.update(node);
    assertThat(isViewClickable(getMountData(mountItem).getDefaultAttributeValuesFlags())).isFalse();
  }
}
