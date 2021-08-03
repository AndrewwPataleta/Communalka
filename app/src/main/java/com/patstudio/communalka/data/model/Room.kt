package com.patstudio.communalka.data.model

import com.google.gson.annotations.SerializedName

data class Room (

    @SerializedName("name") var name : String?,
    @SerializedName("total_area") var totalArea : Double?,
    @SerializedName("living_area") var livingArea : Double?,
    @SerializedName("address") var address : String?,
    @SerializedName("postal_code") var postalCode : String?,
    @SerializedName("country") var country : String?,
    @SerializedName("country_iso_code") var countryIsoCode : String?,
    @SerializedName("federal_district") var federalDistrict : String?,
    @SerializedName("region_fias_id") var regionFiasId : String?,
    @SerializedName("region_kladr_id") var regionKladrId : String?,
    @SerializedName("region_iso_code") var regionIsoCode : String?,
    @SerializedName("region_with_type") var regionWithType : String?,
    @SerializedName("region_type") var regionType : String?,
    @SerializedName("region_type_full") var regionTypeFull : String?,
    @SerializedName("region") var region : String?,
    @SerializedName("city_fias_id") var cityFiasId : String?,
    @SerializedName("city_kladr_id") var cityKladrId : String?,
    @SerializedName("city_with_type") var cityWithType : String?,
    @SerializedName("city_type") var cityType : String?,
    @SerializedName("city_type_full") var cityTypeFull : String?,
    @SerializedName("city") var city : String?,
    @SerializedName("street_fias_id") var streetFiasId : String?,
    @SerializedName("street_kladr_id") var streetKladrId : String?,
    @SerializedName("street_with_type") var streetWithType : String?,
    @SerializedName("street_type") var streetType : String?,
    @SerializedName("street_type_full") var streetTypeFull : String?,
    @SerializedName("street") var street : String?,
    @SerializedName("house_fias_id") var houseFiasId : String?,
    @SerializedName("house_kladr_id") var houseKladrId : String?,
    @SerializedName("house_type") var houseType : String?,
    @SerializedName("house_type_full") var houseTypeFull : String?,
    @SerializedName("house") var house : String?,
    @SerializedName("flat_fias_id") var flatFiasId : String?,
    @SerializedName("flat_type") var flatType : String?,
    @SerializedName("flat_type_full") var flatTypeFull : String?,
    @SerializedName("flat") var flat : String?,
    @SerializedName("fias_id") var fiasId : String?,
    @SerializedName("fias_level") var fiasLevel : String?,
    @SerializedName("kladr_id") var kladrId : String?,
    @SerializedName("timezone") var timezone : String?,
    @SerializedName("geo_lat") var geoLat : String?,
    @SerializedName("geo_lon") var geoLon : String?

)