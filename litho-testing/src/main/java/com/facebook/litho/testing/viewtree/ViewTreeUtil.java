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

package com.facebook.litho.testing.viewtree;

import android.content.res.Resources.NotFoundException;
import androidx.test.core.app.ApplicationProvider;
import com.facebook.infer.annotation.Nullsafe;

/** Utility methods for {@link ViewTreeAssert}. */
@Nullsafe(Nullsafe.Mode.LOCAL)
public final class ViewTreeUtil {

  /**
   * @return the resource name or "<undefined>"
   */
  public static String getResourceName(final int resourceId) {
    try {
      return ApplicationProvider.getApplicationContext()
          .getResources()
          .getResourceEntryName(resourceId);
    } catch (final NotFoundException notFoundException) {
      return "<undefined>";
    }
  }
}
