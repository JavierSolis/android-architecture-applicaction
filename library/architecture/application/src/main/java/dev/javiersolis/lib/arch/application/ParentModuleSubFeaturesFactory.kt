package dev.javiersolis.lib.arch.application

import android.util.Log
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Created by Javier J. Solis Flores on 11/01/22.
 * @email solis.unmsm@gmail.com
 * @github https://github.com/JavierSolis
 */
open class ParentModuleSubFeaturesFactory
{
    val TAG = "ParentSubFeat"
    //ref: https://stackoverflow.com/questions/45210567/how-to-get-a-kotlin-package-by-reflection
    private fun getPackageBase():String{
        val pkg:Package =   this::class.java.`package`
        Log.i(TAG,pkg.name)
        val partPackages = pkg.name.split(".")
        var indexSubFeatures = -1
        partPackages.forEachIndexed { index, s ->
            if(s== Config.packageApplication){
                indexSubFeatures = index
            }
        }

        var packageBase = partPackages[0]
        partPackages.forEachIndexed { index, s ->
            if(index in 1 until indexSubFeatures){
                packageBase = "$packageBase.$s"
            }
        }
        return packageBase
    }

    private fun getClassSubFeatures(packageBase:String): Class<*> {
        return Class.forName(packageBase+".presentation.navigation."+ Config.classSubFeatures)
    }

    fun make(): Module{
        return module{
            val packageBase = getPackageBase()
            val classSubFeatures = getClassSubFeatures(packageBase)
            //val fields = SubFeatures::class.java.declaredFields
            val fields = classSubFeatures.declaredFields
            for (i in fields){
                Log.e("SubFeat ===", i.name)
                if(i.name == "INSTANCE"){
                    continue
                }

                val field  = classSubFeatures.getField(i.name)
                val featureName = field.get(String()) as String
                Log.e("ParentSubFeat.Value ===", featureName)

                val fullClassName = "$packageBase.subFeature.$featureName.application.${featureName.replaceFirstChar { it.uppercase() }}Module"
                Log.e("ParentSubFeat.Pkc ===", fullClassName)
                val cls = Class.forName(fullClassName)
                //val kotlinClass = cls.kotlin
                val obj = cls.newInstance()
                val arguments = arrayOfNulls<Any>(1)
                arguments[0] = this
                cls.methods.forEach {
                    Log.i("ParentSubFeat..",it.name)
                }
                cls.getDeclaredMethod("make",this.javaClass).invoke(obj,this)

            }
        }
    }
}