package hr.algebra.nasa.api

import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("record") val record : List<NasaItem>
)
