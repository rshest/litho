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

package com.facebook.litho.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotated method will be called to create a custom MountContentPool for this mount spec. If a
 * method with this annotation isn't provided, a DefaultMountContentPool will be used to recycle the
 * mount content of this mount spec, which is almost always sufficient.
 *
 * <p>For example:
 *
 * <pre><code>
 *
 *  {@literal @OnCreateMountContentPool}
 *   static MountContentPools.ItemPool OnCreateMountContentPool(int poolSizeOverride) {
 *     return new MountContentPools.ItemPool(poolSizeOverride);
 *   }
 * </code></pre>
 */
@Retention(RetentionPolicy.SOURCE)
public @interface OnCreateMountContentPool {}
