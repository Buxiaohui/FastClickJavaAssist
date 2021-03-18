/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.buxiaohui.qrc

import com.google.common.io.ByteStreams
import com.google.common.io.Files
import javassist.*
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.MethodInfo
import javassist.bytecode.annotation.Annotation
import javassist.bytecode.annotation.LongMemberValue
import javassist.bytecode.annotation.StringMemberValue
import org.apache.http.util.TextUtils
import org.gradle.api.Project

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class InjectGro {
    //初始化类池,以单例模式获取
    private ClassPool pool
    private Project project
    private QrcConfigExtension quickRepeatClickConfig

    InjectGro(Project project) {
        this.pool = ClassPool.default
        this.project = project
    }

    void setQuickRepeatClickConfig(QrcConfigExtension quickRepeatClickConfig) {
        this.quickRepeatClickConfig = quickRepeatClickConfig
    }

    public void appendClassPath(String path) {
        LogUtils.logD("appendClassPath->appendClassPath = " + path)
        pool.appendClassPath(path);
    }

    public void injectJar(String inputPath, String outPutPath) throws NotFoundException, CannotCompileException {
        ArrayList entries = new ArrayList()
        Files.createParentDirs(new File(outPutPath))
        FileInputStream fis = null
        ZipInputStream zis = null
        FileOutputStream fos = null
        ZipOutputStream zos = null
        try {
            fis = new FileInputStream(new File(inputPath))
            zis = new ZipInputStream(fis)
            fos = new FileOutputStream(new File(outPutPath))
            zos = new ZipOutputStream(fos)
            ZipEntry entry = zis.getNextEntry()
            while (entry != null) {
                String fileName = entry.getName()
                if (!entries.contains(fileName)) {
                    entries.add(fileName);
                    zos.putNextEntry(new ZipEntry(fileName));
                    if (!entry.isDirectory()
                            && fileName.endsWith(".class")
                            && !fileName.contains('R$')
                            && !fileName.contains("R.class")
                            && !fileName.contains("BuildConfig.class")) {
                        transformJar(zis, zos, pool)
                    } else {
                        ByteStreams.copy(zis, zos)
                    }
                }
                entry = zis.getNextEntry()
            }
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            if (zos != null) {
                zos.close()
            }
            if (fos != null) {
                fos.close()
            }
            if (zis != null) {
                zis.close()
            }
            if (fis != null) {
                fis.close()
            }
        }
    }

    def printLog = true

    public void injectDir(String inputPath, String outPutPath) throws NotFoundException, CannotCompileException {
        File dir = new File(inputPath)
        LogUtils.logD("inject->inputPath = " + inputPath)
        LogUtils.logD("inject->outPutPath = " + outPutPath)
        //判断如果是文件夹，则遍历文件夹
        if (dir.isDirectory()) {
            //开始遍历
            dir.eachFileRecurse { File file ->
                if (file.isFile()) {
                    String filePath = file.getAbsolutePath()
                    File outPutFile = new File(outPutPath + filePath.substring(inputPath.length()))
                    Files.createParentDirs(outPutFile);
                    if (filePath.endsWith(".class")
                            && !filePath.contains('R$')
                            && !filePath.contains('R.class')
                            && !filePath.contains("BuildConfig.class")) {

                        LogUtils.logD("inject->file = " + file)
                        LogUtils.logD("inject->outPutFile = " + outPutFile)
                        FileInputStream inputStream = new FileInputStream(file)
                        FileOutputStream outputStream = new FileOutputStream(outPutFile)
                        transformDir(inputStream, outputStream, pool)
                    }
                }
            }
        }
    }

    public void transformJar(ZipInputStream inputStream, ZipOutputStream outputStream, ClassPool classPool) {
        try {
            CtClass c = classPool.makeClass(inputStream);
            injectReal(c, classPool);
            outputStream.write(c.toBytecode());
            c.detach()
        } catch (Exception e) {
            e.printStackTrace()
            inputStream.close()
            outputStream.close()
            throw new RuntimeException(e.getMessage())
        }
    }

    public void transformDir(FileInputStream inputStream, FileOutputStream outputStream, ClassPool classPool) {
        try {
            CtClass c = classPool.makeClass(inputStream);
            injectReal(c, classPool);
            outputStream.write(c.toBytecode());
            c.detach()
        } catch (Exception e) {
            e.printStackTrace()
            inputStream.close()
            outputStream.close()
            throw new RuntimeException(e.getMessage())
        }
    }

    public void injectReal(CtClass ctClass, ClassPool classPool) {
        if (ctClass.name.contains("glide")) {
            // LogUtils.logD("injectReal,ctClass:" + ctClass.name)
        }
        if (ctClass.isFrozen()) {
            ctClass.defrost()
        }


        CtMethod[] methods = ctClass.getDeclaredMethods()
        if (methods != null && methods.length > 0) {
            for (CtMethod ctMethod : methods) {
                if (ctMethod != null) {
                    MethodInfo methodInfo = ctMethod.getMethodInfo()
                    if (ctClass.name.equalsIgnoreCase("com.bumptech.glide.request.SingleRequest")
                            && ctMethod.name.equalsIgnoreCase("onResourceReady")) {
                        if(ctMethod.getParameterTypes() != null && ctMethod.getParameterTypes().length == 4){
                            for (i in 0..<ctMethod.getParameterTypes().length) {
                                LogUtils.logD("onResourceReady,i:" + i + ",name:" + ctMethod.getParameterTypes()[i].name)
                            }
                            insertWithGlideSingleRequestResourceReady(methodInfo, ctMethod)
                        }
                    } else if (ctClass.name.equalsIgnoreCase("com.bumptech.glide.load.data.HttpUrlFetcher")
                            && ctMethod.name.equalsIgnoreCase("getStreamForSuccessfulRequest")) {
                        insertWithGlideHttpUrlFetcher(methodInfo, ctMethod)
                    } else if (ctClass.name.equalsIgnoreCase("com.bumptech.glide.load.engine.DecodeJob")
                            && ctMethod.name.equalsIgnoreCase("decodeFromRetrievedData")) {
                        // insertWithGlideDecodeJob(methodInfo, ctMethod)
                    } else if (ctClass.name.equalsIgnoreCase("com.bumptech.glide.load.resource.bitmap.Downsampler")
                            && ctMethod.name.equalsIgnoreCase("decodeFromWrappedStreams")) {
                        // insertWithGlideDownsamplerDecodeStream(methodInfo, ctMethod)
                    } else { // 快速点击
                        if (checkMethod(ctMethod.getModifiers())) {
                            if (methodInfo != null) {
                                insertWithAnnotation(methodInfo, ctMethod)
                            }
                        }
                    }
                }
            }
        }
    }

    private void insertWithGlideHttpUrlFetcher(MethodInfo methodInfo, CtMethod ctMethod) {
        LogUtils.logD("insertWithGlide,methodInfo:" + methodInfo)
        ctMethod.insertBefore("new com.buxiaohui.fastclickjavaassist.DataListener().onHttpDataFetchReady(urlConnection" +
                ".getContentLength()," +
                "glideUrl.toURL(), glideUrl.getHeaders());")

    }

    private void insertWithGlideDecodeJob(MethodInfo methodInfo, CtMethod ctMethod) {
        LogUtils.logD("insertWithGlide,methodInfo:" + methodInfo)
        ctMethod.insertBefore("new com.buxiaohui.fastclickjavaassist.DataListener().onDecodeFromRetrievedData" +
                "(startFetchTime, currentData, currentSourceKey , currentFetcher, currentDataSource, width," +
                " height);")

    }

    private void insertWithGlideSingleRequestResourceReady(MethodInfo methodInfo, CtMethod ctMethod) {
        LogUtils.logD("insertWithGlide,methodInfo:" + methodInfo)
        ctMethod.insertAt(605,"new com.buxiaohui.fastclickjavaassist.DataListener().onResourceReady(result,model, " +
                "target, " +
                "dataSource, isFirstResource);")

    }

    private void insertWithGlideDownsamplerDecodeStream(MethodInfo methodInfo, CtMethod ctMethod) {
        LogUtils.logD("insertWithGlide,methodInfo:" + methodInfo)
        ctMethod.insertAt(416, "new com.buxiaohui.fastclickjavaassist.DataListener().onDownsamplerDecodeStream" +
                "(rotated, sourceWidth, sourceHeight, sourceMimeType , options, downsampled, requestedWidth, " +
                "requestedHeight," +
                "startTime);")

    }

    private void insertWithAnnotation(MethodInfo methodInfo, CtMethod ctMethod) {
        AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo
                .getAttribute(AnnotationsAttribute.visibleTag);
        if (attr != null) {
            Annotation[] annotations = attr.getAnnotations();
            if (annotations != null && annotations.length > 0) {
                for (int j = 0; j < annotations.length; j++) {
                    Annotation annotation = annotations[j]
                    if (annotation != null) {
                        if (quickRepeatClickConfig.annotation.equalsIgnoreCase(annotation.typeName)) {
                            LongMemberValue timeIntervalMemberValue = annotation.getMemberValue(quickRepeatClickConfig.annotationTimeInterval)
                            StringMemberValue tagMemberValue = annotation.getMemberValue(quickRepeatClickConfig.annotationTag)
                            LogUtils.logD("annotation,timeIntervalMemberValue:" + timeIntervalMemberValue)
                            LogUtils.logD("annotation,tagMemberValue:" + tagMemberValue)

//                                            def isTagInvalid = tagMemberValue == null || TextUtils.isEmpty(tagMemberValue.value)
//                                            def isTimeIntervalInvalid = timeIntervalMemberValue == null || timeIntervalMemberValue.value <= 0

                            String tag = tagMemberValue.value
                            long timeInterval = timeIntervalMemberValue.value
//
//                                            if (isTagInvalid && isTimeIntervalInvalid) {
//                                                injectCode = "if (com.buxiaohui.fastclickjavaassist.FastClickUtils.isFastClick()) {\n" +
//                                                        "                                                    return;\n" +
//                                                        "                                                }"
//                                            } else if (isTagInvalid && !isTimeIntervalInvalid) {
//                                                injectCode = " if (com.buxiaohui.fastclickjavaassist.FastClickUtils.isFastClick(${timeInterval}L)) {\n" +
//                                                        "                                                    return;\n" +
//                                                        "                                                }"
//                                            } else if (isTagInvalid && !isTimeIntervalInvalid) {
//                                                injectCode = "if (com.buxiaohui.fastclickjavaassist.FastClickUtils.isFastClick(\"$tag\")) {\n" +
//                                                        "                                                    return;\n" +
//                                                        "                                                }"
//                                            } else if (!isTagInvalid && !isTimeIntervalInvalid) {
//                                                injectCode = "if (com.buxiaohui.fastclickjavaassist.FastClickUtils.isFastClick(\"$tag\",${timeInterval}L)) {\n" +
//                                                        "                                                    return;\n" +
//                                                        "                                                }"
//                                            }
                            String injectCode = quickRepeatClickConfig.insertCodeTemplate
                            injectCode = injectCode.replace(quickRepeatClickConfig.annotationTag, tag)
                            injectCode = injectCode.replace(quickRepeatClickConfig.annotationTimeInterval, "" + timeInterval)
                            LogUtils.logI("injectCode:$injectCode")
                            if (!TextUtils.isEmpty(injectCode)) {
                                ctMethod.insertBefore(injectCode) // 在方法开始注入代码
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean checkMethod(int modifiers) {
        return !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isEnum(modifiers) && !Modifier.isInterface(modifiers)
    }

}