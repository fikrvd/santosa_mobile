package com.example.bagian_c_kotlin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


data class Patient(
    val rm: String,
    val name: String,
    val gender: String
)


@Composable
fun PatientsScreen(){

    var patients by remember {
        mutableStateOf<List<Patient>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf("")
    }


    LaunchedEffect(Unit){

        try {

            val result = withContext(Dispatchers.IO){

                URL(
                    "http://192.168.163.166:9060/api/patients"
                ).readText()

            }


            val json = JSONObject(result)

            val array = json.getJSONArray("data")


            val list = mutableListOf<Patient>()


            for(i in 0 until array.length()){

                val item = array.getJSONObject(i)


                list.add(
                    Patient(
                        rm = item.optString("medical_record_no"),
                        name = item.optString("name"),
                        gender = item.optString("gender")
                    )
                )
            }


            patients = list
            loading = false


        }catch(e:Exception){

            error = e.message ?: "Gagal mengambil data"
            loading = false

        }

    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ){

        Text(
            text = "Data Pasien",
            style = MaterialTheme.typography.headlineMedium
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        if(loading){

            CircularProgressIndicator()

        }else if(error.isNotEmpty()){

            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )

        }else{


            LazyColumn{


                items(patients){ patient ->


                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)

                    ){

                        Column(
                            modifier = Modifier.padding(15.dp)
                        ){

                            Text(
                                "RM : ${patient.rm}"
                            )

                            Text(
                                "Nama : ${patient.name}"
                            )

                            Text(
                                "Gender : ${patient.gender}"
                            )

                        }

                    }


                }


            }

        }


    }

}