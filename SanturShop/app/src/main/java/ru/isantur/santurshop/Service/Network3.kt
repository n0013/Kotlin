//package ru.isantur.santurshop.Service
//
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Intent
//import android.os.IBinder
//import android.util.Log
//import khttp.get
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
//import nl.komponents.kovenant.combine.*
//import nl.komponents.kovenant.task
//import org.json.JSONArray
//import org.json.JSONObject
//import ru.isantur.santurshop.activity.Load
//import ru.isantur.santurshop.Data.*
//import ru.isantur.santurshop.varApp
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import kotlin.collections.ArrayList
//import kotlin.collections.HashMap
//
//class Network3 : Service() {
//    val TAG = "NETWORK"
//    var es: ExecutorService? = null
//
//    override fun onBind(paramIntent: Intent): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        es = Executors.newFixedThreadPool(1)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d(TAG, "$TAG onDestroy")
//    }
//
//    override fun onStartCommand(pi: Intent, flags: Int, startId: Int): Int {
//        Log.d(TAG, "$TAG onStartCommand")
//
//        //Читаем параметры:
//        val PARAM_PI = pi.getParcelableExtra<PendingIntent>("pendingIntent")
//        val PARAM_NETWORK = pi.getIntExtra("network", 0)
//
//
//        val myRun: MyRun = MyRun(PARAM_PI!!, PARAM_NETWORK, startId)
//
//        es!!.execute(myRun)
//
//        return super.onStartCommand(pi, PARAM_NETWORK, startId)
//    }
//
//    internal inner class MyRun(var pi: PendingIntent, var pNetwork: Int, var startId: Int): Runnable {
//
//
//        var arraySales: ArrayList<Sales> = ArrayList<Sales>()
//        var arrayCatalogs: ArrayList<Catalogs> = ArrayList<Catalogs>()
//        var arrayPopulars: ArrayList<Populars> = ArrayList<Populars>()
//        var arrayBrands: ArrayList<Brands> = ArrayList<Brands>()
//        var arrayBanners: ArrayList<Banners> = ArrayList<Banners>()
//        var iam: ArrayList<Iam> = ArrayList<Iam>()
//        var arrayCities: ArrayList<Cities> = ArrayList<Cities>()
//
//
//        var jsArray: JSONArray? = null
//        var jsJSON: JSONObject? = null
//
//
//
//
//
//        var sign = true
//        var sign2 = true
//
//        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//
//        override fun run() {
//
//            try {
//
//
//
//                Log.e("NETWORK2: ","   ===========    START TEST   ===============  " + sdf.format(Date())  )
//
//                while (sign) {
//
//                    if (sign2) {
//                        //success:
//
//
//                        //Connect:
//                        if (varApp.getSessionId_Preferences() != "") {
//                            varApp.setSID( varApp.getSessionId_Preferences() )
//                            varApp.firstLoad = false
//                        } else {
//                            varApp.firstLoad = true
//                            val sessionId_new = get("${varApp.url}connect").text
//                            varApp.setSID( sessionId_new )
//                            varApp.setSessionId_Preferences( sessionId_new )
//                        }
//
//
//                        //Login:
//                        if (varApp.getLogin_Preferences() != "") {
//                            val js = khttp.get("${varApp.url}login/?u=${varApp.getLogin_Preferences()}&p=${varApp.getPassword_Preferences()}&sid=${varApp.SID}").text
//                            if (js.contains("ok")) {
//                                println("login and password correct!!!")
//                            }
//                        }
//
//
//                        combine (
//                            task {  get("${varApp.url}GetSales/").jsonArray },
//                            task {  get("${varApp.url}GetRecomendTKs/").jsonArray },
//                            task {  get("${varApp.url}getbrends/").jsonArray },
//                            task {  get("${varApp.url}getbanners/").jsonArray },
//                            task {  varApp.setiam() },
//                            task { get("${varApp.url}getcities/").jsonArray}
//
//                        ) success {
//
//                        response: Tuple6<JSONArray, JSONArray, JSONArray, JSONArray, Unit, JSONArray> ->
//
//                            //1 Sales:
//                            arraySales = Json.decodeFromString(response.first.toString() )
//                            varApp.setSales(arraySales)
//
//                            //2 Populars:
//                            arrayPopulars = Json.decodeFromString(response.second.toString())
//                            varApp.setPopulars(arrayPopulars)
//
//                            //3 Brands:
//                            arrayBrands = Json.decodeFromString(response.third.toString())
//                            varApp.setBrands(arrayBrands)
//
//                            //4 Banners:
//                            arrayBanners = Json.decodeFromString(response.fourth .toString())
//                            varApp.setBanners(arrayBanners)
//                            //5 setIam:
//
//                            //6 Cities:
//                            arrayCities = Json.decodeFromString(response.sixth.toString())
//                            varApp.setCities(arrayCities)
//
//                            broadcastResult ("ok")
//
//
//                        } fail { errors ->
//                            broadcastResult("error")
//                        }
//
//
//
//
//                        sign2 = false
//
//
//                    } //if sign
//
//
//
//                } //while
//
//
//
//
//            } catch (e: Exception) {
////                println(e.stackTrace)
//                println(e.message)
//                broadcastResult ("error")
//            }
//
//            stop()
//        }
//
//        fun broadcastResult (result: String) {
//            sign = false
//
//            val intent = Intent()
//                .putExtra("from", "network")
//                .putExtra("result", result)
//                .putExtra("sign", false)
//            pi.send(this@Network3, Load.STATUS_FINISH, intent)
//            stop()
//        }
//
//        fun stop() {
//            Log.d(TAG, "$TAG stop")
//            stopSelfResult(startId)
//        }
//
//
//
//        fun AddGroupAndGroupItemCatalogs () {
//
//            if (varApp.getCatalogsGroupCount() == 0) {
//
//                val groups = varApp.catalogs.filter { it.parent_id == 0 }
//
//                // коллекция для групп
//                val groupData: ArrayList<Map<String, String>> = ArrayList()
//
//
//                // список атрибутов группы или элемента
//                lateinit var m: Map<String, String>
//
//
//                // заполняем коллекцию групп из массива с названиями групп
//                for (group in groups) {
//                    // заполняем список атрибутов для каждой группы
//                    m = HashMap()
//                    m.put("groupName", group.name)
//                    groupData.add(m)
//                }
//
//
//                varApp.setCatalogsGroup(groupData)
//
//                // общая коллекция для коллекций элементов
//                val childData: ArrayList<ArrayList<Map<String, String>>> = ArrayList()
//
//                // создаем коллекцию для коллекций элементов
//
//
//                //region create Catalogs Group Item:
//                // коллекция для элементов одной группы
//                var childDataItem: ArrayList<Map<String, String>>?
//
//                for (group in groups) {
//
//                    val groupsSub = varApp.catalogs.filter {
//                        it.parent_id == group.id
//                    }
//
//                    childDataItem = ArrayList()
//
//                    for (grSub in groupsSub) {
//                        m = HashMap()
//                        m.put("nameSub", grSub.name)
//                        m.put("idSub", grSub.id.toString())
//                        childDataItem.add(m)
//                    }
//
//                    childData.add(childDataItem)
//
//                }
//
//
//                varApp.setCatalogsGroupItem(childData)
//
//            }
//
//        } //fun AddGroupAndGroupItemCatalogs
//
//
//
//    }
//
//} //Network3
//
