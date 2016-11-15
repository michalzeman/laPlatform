package com.la.platform.batch.ml


import com.la.platform.batch.common.SparkUtils
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils.loadLibSVMFile
import org.apache.spark.rdd.RDD

import scala.annotation.tailrec

/**
  * Created by zemi on 10/11/2016.
  */
object SvmIOUtils extends SparkUtils {


  //TODO: optimize names could be millions, better it would be to wrap into RDD!!!
  def loadDDRLibSVMFiles(names:List[String]): RDD[LabeledPoint] = names match {
    case head::tail => {
      @tailrec
      def loadDDRLibSVMFile(accRdd: RDD[LabeledPoint], namesFiles: List[String]): RDD[LabeledPoint] = namesFiles match {
        case name :: namesTail => loadDDRLibSVMFile(accRdd ++ loadLibSVMFile(getSparkContext, name),namesTail)
        case Nil => accRdd
      }
      loadDDRLibSVMFile(loadLibSVMFile(getSparkContext, names.head), names.tail)
    }
    case Nil => SparkContext.getOrCreate().emptyRDD
  }

  /**
    * Load SVM data from files
    * @param nameDir - directory in the wildcard form like ''repo/laPlatform/resources/mllib/lg_svm-*''
    * @return RDD[LabeledPoint]
    */
  def loadDDRLibSVMFiles(nameDir: String): RDD[LabeledPoint] = {
    val sc = getSparkContext
    sc.wholeTextFiles(nameDir, 2)
      .filter(!_._2.isEmpty).map(_._1)
      .flatMap(fileName => loadLibSVMFile(getSparkContext, fileName).collect())
  }

}
