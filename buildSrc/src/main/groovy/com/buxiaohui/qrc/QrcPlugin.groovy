/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.buxiaohui.qrc

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class QrcPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (project != null) {
            AppExtension appExtension = project.extensions.getByType(AppExtension.class)
            QrcTransform quickRepeatClickTransformGro = new QrcTransform(project)
            appExtension.registerTransform(quickRepeatClickTransformGro)
            project.extensions.create("qrcConfig", QrcConfigExtension.class)
            project.afterEvaluate {
                quickRepeatClickTransformGro.quickRepeatClickConfig = project.qrcConfig
            }
        }
    }
}