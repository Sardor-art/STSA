package hamidov.sardor.stsa.apiHandler

import hamidov.sardor.stsa.utils.FeaturedPhoto
import hamidov.sardor.stsa.utils.Sections
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
// parent_id=5&depth=1&include=FeaturedPhoto&limit=23
interface JsonPlaceHolderApi {
    @GET("specialty-subject?")
    fun getPosts(
        @Query("parent_id") parentID: Int,
        @Query("depth") depth: Int,
        @Query("include") include:String,
        @Query("limit") limit :Int

        ): Call<Sections>

}