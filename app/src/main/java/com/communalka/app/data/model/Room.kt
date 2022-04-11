package com.communalka.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "room")
@Parcelize
data class Room (
    @PrimaryKey var id: String,
    @SerializedName("name") var name : String?= null,
    @SerializedName("total_area") var totalArea : Double?= null ,
    @SerializedName("living_area") var livingArea : Double?= null,
    @SerializedName("address") var address : String?= null,
    @SerializedName("postal_code") var postalCode : String?= null,
    @SerializedName("country") var country : String?= null,
    @SerializedName("consumer") var consumer : String?= null,
    @SerializedName("fio") var fio : String?= null,
    @SerializedName("country_iso_code") var countryIsoCode : String?= null,
    @SerializedName("federal_district") var federalDistrict : String?= null,
    @SerializedName("region_fias_id") var regionFiasId : String?= null,
    @SerializedName("region_kladr_id") var regionKladrId : String?= null,
    @SerializedName("region_iso_code") var regionIsoCode : String?= null,
    @SerializedName("region_with_type") var regionWithType : String?= null,
    @SerializedName("region_type") var regionType : String?= null,
    @SerializedName("region_type_full") var regionTypeFull : String?= null,
    @SerializedName("region") var region : String?= null,
    @SerializedName("city_fias_id") var cityFiasId : String?= null,
    @SerializedName("city_kladr_id") var cityKladrId : String?= null,
    @SerializedName("city_with_type") var cityWithType : String?= null,
    @SerializedName("city_type") var cityType : String?= null,
    @SerializedName("city_type_full") var cityTypeFull : String?= null,
    @SerializedName("city") var city : String?= null,
    @SerializedName("street_fias_id") var streetFiasId : String?= null,
    @SerializedName("street_kladr_id") var streetKladrId : String?= null,
    @SerializedName("street_with_type") var streetWithType : String?= null,
    @SerializedName("street_type") var streetType : String?= null,
    @SerializedName("street_type_full") var streetTypeFull : String?= null,
    @SerializedName("street") var street : String?= null,
    @SerializedName("house_fias_id") var houseFiasId : String?= null,
    @SerializedName("house_kladr_id") var houseKladrId : String?= null,
    @SerializedName("house_type") var houseType : String?= null,
    @SerializedName("house_type_full") var houseTypeFull : String?= null,
    @SerializedName("house") var house : String?= null,
    @SerializedName("flat_fias_id") var flatFiasId : String?= null,
    @SerializedName("flat_type") var flatType : String?= null,
    @SerializedName("flat_type_full") var flatTypeFull : String?= null,
    @SerializedName("flat") var flat : String?= null,
    @SerializedName("fias_id") var fiasId : String?= null,
    @SerializedName("fias_level") var fiasLevel : String?= null,
    @SerializedName("kladr_id") var kladrId : String?= null,
    @SerializedName("timezone") var timezone : String?= null,
    @SerializedName("geo_lat") var geoLat : String?= null,
    @SerializedName("geo_lon") var geoLon : String?= null,
    @SerializedName("created_date") var createdDate : String?= null,
    @Expose
    @SerializedName("firstSave") var firstSave : Boolean?= false,
    @Expose
    @SerializedName("image_type")
    var imageType: String,
    @Expose
    @SerializedName("image_path")
    var imagePath: String,

): Parcelable {
  

}

