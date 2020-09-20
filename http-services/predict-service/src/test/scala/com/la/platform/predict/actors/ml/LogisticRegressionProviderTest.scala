package com.la.platform.predict.actors.ml

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

/**
  * Created by zemi on 08/10/2017.
  */
class LogisticRegressionProviderTest extends TestKit(ActorSystem("predict-http-service-PredictActionActorTest"))
  with FunSuiteLike
  with BeforeAndAfterAll
  with Matchers
  with ImplicitSender
  with MockitoSugar {

  val dataFalse = "1:1.0 2:1.0 3:0.70687 4:-0.54781 5:-0.3872304547 6:-0.273721591513789 7:0.30009579610000003 8:0.21212871538920702 9:0.14994742504716876 10:0.10599333634309219 11:-0.164395478061541 12:-0.11620623157736149 13:-0.08214269891508952 14:-0.058064209582109325 15:-0.04104384782730562 16:0.09005748683689278 17:0.0636589357203944 18:0.04499859189267519 19:0.03180815465117531 20:0.022484230278276292 21:0.015893427856805163 22:-0.04933439186411824 23:-0.03487300157698926 24:-0.024650678624726398 25:-0.01742482519946035 26:-0.012317086188742537 27:-0.008706578714236437 28:-0.0061544192957323106"

  val dataTrue = "1:1.0 2:1.0 3:0.57529 4:0.13767 5:0.07920017429999998 6:0.045563068273046986 7:0.018953028899999995 8:0.010903487995880996 9:0.006272667609150378 10:0.0036086029488681206 11:0.002609263488662999 12:0.0015010831923929367 13:8.635581497517324E-4 14:4.967963679706741E-4 15:2.858019825298491E-4 16:3.592173044842351E-4 17:2.066541230967356E-4 18:1.1888605047632101E-4 19:6.839395597852271E-5 20:3.934635893488433E-5 21:2.2635566831649603E-5 22:4.945344630834464E-5 23:2.8450073126727588E-5 24:1.636704256907511E-5 25:9.41579591956322E-6 26:5.416813234565525E-6 27:3.116238485713201E-6 28:1.7927408384459469E-6"

  val stub = LogisticRegressionProviderBuilder.get.build(this.testActor, system)

  test("Predict 0.0") {
    assertResult("0.0", "Error result expected")(stub.predict(dataFalse).get)
  }

  test("Predict 1.0") {
    assertResult("1.0", "Error result expected")(stub.predict(dataTrue).get)
  }

}