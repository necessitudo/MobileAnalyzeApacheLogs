package com.example.olegdubrovin.analyzeapachelogs.common

import android.util.Log
import de.blox.treeview.TreeNode
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.util.*
import android.os.Environment
import com.example.olegdubrovin.analyzeapachelogs.data.LineTree


fun getRootNode(nameFile:String):TreeNode {

    val treeData = HashMap<String?, HashMap<String?, TreeMap<Int, Any>>>()

    val pathToDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val pathToFile = "$pathToDownload/$nameFile"

    Files.lines(Paths.get(pathToFile), StandardCharsets.UTF_8).forEach {

        try {
            recoverMainBranch(it, treeData)
        } catch ( e:Exception) {
            println(it)
        }

    }

    val lineRoot =  LineTree("Overal", 0)
    val rootNode = TreeNode(lineRoot.toString())

    for (i in treeData.keys) treeScanDown(i!!, treeData, rootNode)

    return rootNode

}

fun recoverMainBranch(contentStr: String?, treeData: HashMap<String?, HashMap<String?, TreeMap<Int, Any>>>) {

    val wordDate = Regex(Patterns.DATE.expression).find(contentStr!!)

    if (wordDate != null) {

        val year = Regex(Patterns.YEAR.expression).find(wordDate.value)?.value
        val month = Regex(Patterns.MONTH.expression).find(wordDate.value)?.value
        val day = Regex(Patterns.DAY.expression).find(wordDate.value)?.value!!.toInt()

        if (!treeData.containsKey(year)) treeData.put(year, HashMap<String?, TreeMap<Int,Any>>())
        val lineYear = treeData.get(year)

        if (lineYear != null && !lineYear.containsKey(month)) lineYear.put(month, TreeMap<Int, Any>())
        val lineMonth = lineYear?.get(month)

        if (lineMonth != null && !lineMonth.containsKey(day)) lineMonth.put(day, HashMap<String?, Any>())
        val lineDay = lineMonth?.get(day) as HashMap<String?, Any>

        val collectionContent = contentStr.split("/")

        if (collectionContent.size>=6) {

            var previousBranch = HashMap<String, Any>()
            //val amountIteration = collectionContent.size-2
            val amountIteration = collectionContent.size-4

            for (n in 3..amountIteration) {

                val currentValue = collectionContent.get(n)

                when (n){
                    3 ->{
                        //First iteration
                        previousBranch = if (lineDay.containsKey(currentValue)){
                            lineDay.get(currentValue)
                        }else{
                            val currentBranch = HashMap<String, Any>()
                            lineDay.put(currentValue, currentBranch)
                            currentBranch
                        } as HashMap<String, Any>

                    }
                    amountIteration ->{
                        //It final iteration! Final element contain key and counter of calling
                        if (previousBranch.containsKey(currentValue)) {
                            var previousCounter = previousBranch.get(currentValue) as Int
                            previousBranch.put(currentValue, previousCounter+1)
                        } else {
                            val currentCounter = 1 as Int
                            previousBranch.put(currentValue, currentCounter)
                        }
                    }

                    else ->{
                        val currentBranch =  if (previousBranch.containsKey(currentValue)){
                            previousBranch.get(currentValue) as HashMap<String, Any>
                        } else {
                            val currentBranchTemp = HashMap<String, Any>()
                            previousBranch.put(currentValue, currentBranchTemp)
                            currentBranchTemp
                        }
                        previousBranch = currentBranch

                    }
                }
            }
        }
    }
}

fun treeScanDown(key: Any, treeData: Map<*, *>, rootNode: TreeNode){

    val currentValue = treeData.get(key)

    val line = LineTree(if (key is String ) key else key.toString(), if (currentValue is Int) currentValue else 0)
    val lineNode = TreeNode(line.toString())
    rootNode.addChild(lineNode)

    //На каждой итерации возвращаемся вверх и пересчитываем итоги

    //Спустимся вниз в кроличью нору


    if(currentValue is Map<*,*>) {
        for (i in currentValue.keys) treeScanDown(i!!, currentValue, lineNode)
    }

    //if Int to going rescan up!
    if (currentValue is Int) {
        treeScanUp(currentValue, rootNode)
    }

}

fun treeScanUp(addValue: Int, rootNode: TreeNode){

    //rootNode.data value.amount += addValue

    //rootNode.value.amount += addValue

    if (rootNode.parent!=null) treeScanUp(addValue, rootNode.parent)
}

