package com.example.olegdubrovin.analyzeapachelogs.data

data class LineTree(val branch:String?, var amount:Int){

    override fun toString(): String { return branch+"-"+amount.toString() }
}