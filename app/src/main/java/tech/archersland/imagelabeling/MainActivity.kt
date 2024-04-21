package tech.archersland.imagelabeling

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import tech.archersland.imagelabeling.ui.theme.ImageLabelingTheme
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val fileName = "camaro_z11.png"

            // get bitmap from assets folder
            val bitmap: Bitmap? = assetsToBitmap(fileName)

            ImageLabelingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var outputText by remember {
                        mutableStateOf("")
                    }
                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    val image = bitmap?.let {
                        InputImage.fromBitmap(it, 0)
                    }


                    fun process() {
                        if (image != null) {
                            labeler.process(image).addOnSuccessListener { labels ->
                                labels.forEach { label ->
                                    val text = label.text
                                    val confidence = label.confidence
                                    outputText += "$text : $confidence\n"
                                }


                            }
                        }
                    }

                    Column {

                        bitmap?.let {
                            Image(bitmap = it.asImageBitmap(), contentDescription = "image")
                        }


                        Text(text = outputText)

                        Button(onClick = { process() }) {
                            Text(text = "Label image")
                        }
                    }
                }
            }
        }
    }


}

fun Context.assetsToBitmap(fileName: String): Bitmap? {
    return try {
        with(assets.open(fileName)) {
            BitmapFactory.decodeStream(this)
        }
    } catch (e: IOException) {
        null
    }
}
