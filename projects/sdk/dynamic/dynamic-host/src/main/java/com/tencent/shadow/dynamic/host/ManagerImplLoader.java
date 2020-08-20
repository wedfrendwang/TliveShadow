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
import android.util.Log;

import com.tencent.shadow.core.common.InstalledApk;

import java.io.File;

final class ManagerImplLoader extends ImplLoader {
    private static final String MANAGER_FACTORY_CLASS_NAME = "com.tencent.shadow.dynamic.impl.ManagerFactoryImpl";
    private static final String[] REMOTE_PLUGIN_MANAGER_INTERFACES = new String[]
            {
                    "com.tencent.shadow.core.common",
                    "com.tencent.shadow.dynamic.host"
            };
    final private Context applicationContext;
    final private InstalledApk installedApk;

    ManagerImplLoader(Context context, File apk, String libPath) {
        applicationContext = context.getApplicationContext();
        File root = new File(applicationContext.getFilesDir(), "ManagerImplLoader");
        File odexDir = new File(root, Long.toString(apk.lastModified(), Character.MAX_RADIX));
        odexDir.mkdirs();
        // 获取对用路径
        libPath = extractSo(applicationContext.getFilesDir(),apk,libPath).getAbsolutePath();
        installedApk = new InstalledApk(apk.getAbsolutePath(), odexDir.getAbsolutePath(), libPath);
    }

    PluginManagerImpl load() {
        ApkClassLoader apkClassLoader = new ApkClassLoader(
                installedApk,
                getClass().getClassLoader(),
                loadWhiteList(installedApk),
                1
        );

        Context pluginManagerContext = new ChangeApkContextWrapper(
                applicationContext,
                installedApk.apkFilePath,
                apkClassLoader
        );

        try {
            ManagerFactory managerFactory = apkClassLoader.getInterface(
                    ManagerFactory.class,
                    MANAGER_FACTORY_CLASS_NAME
            );
            return managerFactory.buildManager(pluginManagerContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    String[] getCustomWhiteList() {
        return REMOTE_PLUGIN_MANAGER_INTERFACES;
    }


    /**
     * 取出插件apk中的so资源，放置在对应文件夹下
     * @param root  资源存放的位置   data/user/0/packageName/files
     * @param apk   插件apk存在位置
     * @param libPath  apk中so资源存放 相对位置   lib/armeabi/
     * @return
     */
    public final File extractSo(File root, File apk, String libPath)  {
        String apkName = apk.getName();// xxx.apk
        long modify = apk.lastModified(); // 当前文件装入系统最后时间
        String fileName = apkName.substring(0,apkName.indexOf(".")); // xxx
        File soDir = new File(root, fileName+"_lib");// apk中所有的so文件将会被存储在xxx_lib 文件夹下
        File soPath = null;
        try {
            // 将apk解压，取出 lib/armeabi/ 下的so文件，并将其放置在 soDir路径下
            soPath = CopySoBloc.copySo(apk, soDir
                    , new File(soDir,fileName + "_" + modify), libPath);
        } catch (InstallPluginException e) {
            e.printStackTrace();
            Log.d("tliveplay  log", "插件解压异常: ");
        }

        return soPath;
    }

}
