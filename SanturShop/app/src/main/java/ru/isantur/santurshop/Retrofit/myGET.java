package ru.isantur.santurshop.Retrofit;


import com.google.gson.JsonObject;


import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import ru.isantur.santurshop.Data.Banners;
import ru.isantur.santurshop.Data.Brands;
import ru.isantur.santurshop.Data.Cart;
import ru.isantur.santurshop.Data.Catalogs;
import ru.isantur.santurshop.Data.Cities;
import ru.isantur.santurshop.Data.Company;
import ru.isantur.santurshop.Data.Details;
import ru.isantur.santurshop.Data.DiscountCart;
import ru.isantur.santurshop.Data.Favorites;
import ru.isantur.santurshop.Data.Iam;
import ru.isantur.santurshop.Data.ListGoods;
import ru.isantur.santurshop.Data.OrderList;
import ru.isantur.santurshop.Data.Populars;
import ru.isantur.santurshop.Data.Products;
import ru.isantur.santurshop.Data.Sales;

import retrofit2.http.Query;
import ru.isantur.santurshop.Data.Search;
import ru.isantur.santurshop.Data.tntks;


public interface myGET {


    //region oldCode (connect, banners, sales, brands, recomend)

    @GET("connect")
    Observable<String> connect();


    @GET("login")
    Observable<String> login (
            @Query("u") String u,
            @Query("p") String p
    );

    //logout:
    @GET("logout")
    Observable<String> logout(@Query("sid") String sid );

    //iam
    @GET("getiam")
    Observable<Iam> iam(@Query("sid") String sid );




    @GET("getbanners")
    Observable<ArrayList<Banners>> getbanners();


    @GET(" ")
    Observable<String> setOk();

    //Рекомендуем товары:
    @GET("GetSales")
    Observable<ArrayList<Sales>> getSales();

    //brands:
    @GET("getbrends")
    Observable<ArrayList<Brands>> getbrends();

    //Популярные категории:
    @GET("GetRecomendTKs")
    Observable<ArrayList<Populars>> getRecomendTKs();

    //endregion









    //catalogs:
    @GET("getcatalog")
    Observable<ArrayList<Catalogs>> getCatalog(@Query("sid") String sid );










    //product
    //data:
    @GET("GetGoodCart")
    Observable<ListGoods> getGoodCart(
                                     @Query("gcode") String gcode,
                                     @Query("sid") String sid
    );

    //chars:
    @GET("GetGoodChars")
    Observable<ArrayList<JsonObject>> getGoodChars(
                                        @Query("gcode") String gcode,
                                        @Query("sid") String sid
    );


    //related:
    @GET("GetGoodAnG")
    Observable<ArrayList<JsonObject>> getGoodAnG(
                                        @Query("gcode") String gcode,
                                        @Query("sid") String sid
    );










    //favorites:
    @GET("GetFavorities")
    Observable<ArrayList<Favorites>> getFavorite(@Query("sid") String sid );

    //remove favorite item:
    @GET("FavorityRemove")
    Observable<String> removeFavorite(@Query("gcode") String gcode,
                                      @Query("sid") String sid
    );

    //add favorite item
    @GET("FavorityAdd")
    Observable<String> addFavorite(@Query("gcode") String gcode,
                                      @Query("sid") String sid
    );








    //cart:
    @GET("getcart")
    Observable<Cart> getCart(@Query("sid") String sid );

    //clear cart:
    @GET("clearcart")
    Observable<String> clearCart(@Query("sid") String sid );

    //add to cart:
    @GET("addtocart")
    Observable<String> addToCart(@Query("gcode") String gcode,
                                 @Query("qty") String qty,
                                 @Query("sid") String sid
                                 );

    //remove cart item:
    @GET("RemoveCartItem")
    Observable<String> removeCartItem(@Query("gcode") String gcode,
                                      @Query("sid") String sid
    );

    //change cart item:
    @GET("ChangeCartItem")
    Observable<String> changeCartItem(@Query("gcode") String gcode,
                                      @Query("qty") String qty,
                                      @Query("sid") String sid
    );









    //select city:
    @GET("SelectCity")
    Observable<String> selectCity(@Query("city") String city,
                                      @Query("sid") String sid
    );


    //orders:
//    GetOrders(string sid = "", string ldate = "", string rdate = "", string search = "", string state = "", int page = 1)
    @GET("GetOrders")
    Observable<OrderList> getOrders(
            @Query("sid") String sid
    );


    //getord:
    @GET("getord")
    Observable<Details> getord(
                            @Query("id") String id,
                            @Query("sid") String sid
    );



    //CardToOrd:
//    CardToOrd(string tip = "p", string place = "100000", string address = "", string pay = "бн", string cmnt = "", string sid = "")









    //setGoodSearch:
    @GET("setgoodsearch")
    Observable<String> setGoodSearch(@Query("brend") String brend,
                                     @Query("tn_id") String tn_id,
                                     @Query("tk_id") String tk_id,
                                     @Query("sid") String sid
    );

    //getGoodList:
    @GET("getgoodlist")
    Observable<Products> getGoodList(@Query("page") String page,
                                                @Query("sid") String sid
    );


    //quickSearch:
    @GET("quicksearch")
    Observable<tntks> quickSearch(@Query("search") String search,
                                  @Query("sid") String sid
    );


    //clearfilter:
    @GET("clearfilter")
    Observable<tntks> clearFilter(@Query("flt") String flt,
                                  @Query("sid") String sid
    );






    //GetDChistory:
    @GET("GetDCInfo")
    Observable<DiscountCart> GetDCInfo(
            @Query("dc") String dc,
            @Query("sid") String sid
    );





//    //Filter catalog:
//    @GET("setgoodsearch/")
//    Observable<String> setgoodsearch(@Header("Cookie") String Cookie,
//                                     @Query("tn_id") String tn_id
////                                     @Query("tn_id") String tn_id
//    );


//    //product List:
//    @GET("getgoodlist/{?page=}")
//    Observable<JsonObject> getgoodlist(@Header("Cookie") String Cookie,
//                                       @Query("?page=") Integer page
//    );



    @GET("getcities")
    Observable<ArrayList<Cities>> getcities();

    //Profile:
    @GET("getiam")
    Observable<Iam> getiam();


    //changeprofile/?name=имя&phone=телефон&email=мыло&sid=
    @GET("changeprofile")
    Observable<String> changeprofile(
            @Query("name") String name,
            @Query("phone") String phone,
            @Query("email") String email,
            @Query("sid") String sid

    );


//    changepsw/?oldpsw=старыйпароль&newpsw=новый&sid=
    @GET("changepsw")
    Observable<String> changepsw(
            @Query("oldpsw") String oldpsw,
            @Query("newpsw") String newpsw,
            @Query("sid") String sid

    );


    //info:
    @GET("GetAbout")
    Observable<String> AboutCompany(@Query("sid") String sid );


    @GET("GetDelivery")
    Observable<String> delivery(@Query("sid") String sid );


    @GET("GetPaypolitic")
    Observable<String> paypolitic(@Query("sid") String sid );


    //companyinfo:
    @GET("companyinfo")
    Observable<ArrayList<Company>> companyinfo(@Query("sid") String sid );






}