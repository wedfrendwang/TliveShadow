/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.view.View;

import dalvik.system.BaseDexClassLoader;

/**
 * 与宿主间通信接口
 */
public interface EnterCallback {

    /*
    插件加载过渡接口，展示加载过程VIEW
     */
    void onShowLoadingView(View view);

    /*
    关闭加载View
     */
    void onCloseLoadingView();

    /*
    插件加载完成
     */
    void onEnterComplete();

    /*
    加载插件
    classLoader：加载插件classLoader
    contextPlugin：插件context
     */
    void onPluginClassLoad(BaseDexClassLoader classLoader, Context contextPlugin);
}
