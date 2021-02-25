package ru.isantur.santurshop.diimport android.annotation.SuppressLintimport android.os.StrictModeimport com.google.android.material.badge.BadgeDrawableimport com.google.gson.Gsonimport com.google.gson.GsonBuilderimport dagger.Moduleimport dagger.Providesimport io.reactivex.Observableimport io.reactivex.android.schedulers.AndroidSchedulersimport io.reactivex.disposables.CompositeDisposableimport io.reactivex.schedulers.Schedulersimport khttp.getimport okhttp3.OkHttpClientimport retrofit2.Retrofitimport retrofit2.adapter.rxjava2.RxJava2CallAdapterFactoryimport retrofit2.converter.gson.GsonConverterFactoryimport retrofit2.converter.scalars.ScalarsConverterFactoryimport ru.isantur.santurshop.AppResponseimport ru.isantur.santurshop.Rimport ru.isantur.santurshop.Retrofit.myGETimport ru.isantur.santurshop.varAppimport java.util.concurrent.TimeUnitimport javax.inject.Injectimport javax.inject.Singleton@Modulepublic class BackendModule {    var disposeBag: CompositeDisposable    @Inject    constructor() {        disposeBag = provideCompositeDisposable()    }    @Provides    @Singleton    fun provideGson(): Gson = GsonBuilder().create()    @Provides    @Singleton    fun provideOkHttpClient(): OkHttpClient =        OkHttpClient.Builder()            .readTimeout(60L, TimeUnit.SECONDS)            .connectTimeout(60L, TimeUnit.SECONDS)            .build()    @Provides    @Singleton    fun provideRetrofit (gson: Gson? = provideGson(), okHttpClient: OkHttpClient? = provideOkHttpClient(), url: String = "http://isantur.ru/mobile/"): Retrofit =        Retrofit.Builder()            .baseUrl(url)            .addConverterFactory(ScalarsConverterFactory.create())            .addConverterFactory(GsonConverterFactory.create(gson))            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())            .client(okHttpClient)            .build()    @Provides    @Singleton    fun provideMyGet (): myGET {        return provideRetrofit().create(myGET::class.java)    }    @Provides    @Singleton    fun provideCompositeDisposable (): CompositeDisposable {        return CompositeDisposable()    }//    @Provides//    @Singleton//    fun clearFilter () : String {//        get( "${varApp.url}setgoodsearch/?tn_id=0&sid=${varApp.SID}")//        get( "${varApp.url}clearfilter/?flt=all&sid=${varApp.SID}")//        return "clear"//    }//    @Provides//    @Singleton//    fun policy (): String {//        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()//        StrictMode.setThreadPolicy(policy)//        return "ok"//    }    @SuppressLint("CheckResult")    fun getCartBadge () : AppResponse {        var response = AppResponse.onSuccess        provideMyGet().getCart(varApp.SID)            .delay(varApp.delay_default, varApp.typeUnit)            .subscribeOn(Schedulers.io())            .observeOn(AndroidSchedulers.mainThread())            .subscribe ({                if (it.qtyitems != 0) {                    varApp.addBadge( R.id.navigation_cart, it.qtyitems )                } else {                    varApp.removeBadge(R.id.navigation_cart)                }                response = AppResponse.onSuccess            }, {                response = AppResponse.noInternet            })        return response    }    @SuppressLint("CheckResult")    fun getFavoriteBadge (): AppResponse {        var response = AppResponse.onSuccess        provideMyGet().getFavorite(varApp.SID)            .delay(varApp.delay_default, varApp.typeUnit)            .subscribeOn(Schedulers.io())            .observeOn(AndroidSchedulers.mainThread())            .subscribe ({                if (it.size != 0) {                    varApp.addBadge( R.id.navigation_favorite, it.size)                } else {                    varApp.removeBadge(R.id.navigation_favorite)                }                response = AppResponse.onSuccess            }, {                response = AppResponse.noInternet            })        return response    }}