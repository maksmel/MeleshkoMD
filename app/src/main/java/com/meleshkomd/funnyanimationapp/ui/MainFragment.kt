package com.meleshkomd.funnyanimationapp.ui

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.json.responseJson
import com.meleshkomd.funnyanimationapp.R
import com.meleshkomd.funnyanimationapp.data.ResponseStatus
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

open class MainFragment : Fragment(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main
    private var cachedFiles: MutableList<String> = mutableListOf()
    private var descriptionsFiles: MutableList<String> = mutableListOf()
    private val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
    private var fileIndex = -1
    private var pageCounter = 0
    private var resultCounter = 0
    private var lastPictureIndex = -1
    private var resultObject = JSONArray()

    protected var category = ""
    protected lateinit var imageView: ImageView
    protected lateinit var textView: TextView

    protected lateinit var circularProgressDrawable: CircularProgressDrawable

    protected fun getCurrentGif() {
        if (fileIndex < 0) {
            getNextGif()
        } else if (lastPictureIndex < 0 || lastPictureIndex >= 0 && fileIndex < lastPictureIndex) {
            ++fileIndex
            getPreviousGif()
        } else {
            textView.text = "В данной категории больше ничего нет"
            imageView.setImageResource(R.drawable.ic_camera_alt_24px)
        }
    }

    protected open fun getNextGif() {
        try {
            ++fileIndex
            if (fileIndex < cachedFiles.size) {
                textView.text = descriptionsFiles[fileIndex]
                Glide.with(this).asGif()
                    .placeholder(circularProgressDrawable)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .load(cachedFiles[fileIndex])
                    .transform(RoundedCorners(15))
                    .error(R.drawable.ic_broken_image_24px)
                    .apply(requestOptions)
                    .into(imageView)
                return
            }
            fileIndex = cachedFiles.size
            while (resultCounter >= resultObject.length()) {
                resultCounter = 0
                lateinit var response: ResponseStatus
                runBlocking {
                    val job = launch {
                        response = getRequest()
                    }
                    job.join()
                }
                var flag = false
                if (response.getException() == null) {
                    val (bytes, error) = response.getResponse()
                    if (bytes == null || error != null) {
                        flag = true
                    }
                }
                if (flag || response.getException() != null) {
                    val myToast = Toast.makeText(
                        requireContext(),
                        "Не удалось подключиться к сайту, проверьте подключение к интернету " +
                                "и попробуйте ещё раз",
                        Toast.LENGTH_LONG
                    )
                    myToast.show()
                    --fileIndex
                    return
                }
                val resp = response.getResponse()
                ++pageCounter
                val obj: JSONObject = resp.get().obj()
                val totalCount = obj.getString("totalCount").toInt()
                if (totalCount == 0) {
                    textView.text = "В этой категории больше ничего нет"
                    imageView.setImageResource(R.drawable.ic_camera_alt_24px)
                    if (lastPictureIndex < 0 || lastPictureIndex >= 0 && lastPictureIndex > fileIndex) {
                        lastPictureIndex = fileIndex
                    }
                    return
                }
                resultObject = obj["result"] as JSONArray
            }
            val currGif = resultObject[resultCounter] as JSONObject
            ++resultCounter
            val gifUrl = currGif.getString("gifURL")
            val description = currGif.getString("description")
            textView.text = description
            Glide.with(this).asGif()
                .placeholder(circularProgressDrawable)
                .transition(DrawableTransitionOptions.withCrossFade())
                .load(gifUrl)
                .transform(RoundedCorners(15))
                .error(R.drawable.ic_broken_image_24px)
                .apply(requestOptions)
                .into(imageView)
            cachedFiles.add(gifUrl)
            descriptionsFiles.add(description)
        } catch (e: Exception) {
            val myToast = Toast.makeText(
                requireContext(),
                "Что-то пошло не так. Попробуйте снова нажать на стрелку",
                Toast.LENGTH_LONG
            )
            myToast.show()
            if (fileIndex < cachedFiles.size) {
                cachedFiles.removeAt(fileIndex)
            }
            if (fileIndex < descriptionsFiles.size && cachedFiles.size != descriptionsFiles.size) {
                descriptionsFiles.removeAt(fileIndex)
            }
            getPreviousGif()
        }
    }

    protected fun getPreviousGif() {
        if (fileIndex <= 0) return

        --fileIndex
        try {
            textView.text = descriptionsFiles[fileIndex]
            Glide.with(this).asGif()
                .placeholder(circularProgressDrawable)
                .transition(DrawableTransitionOptions.withCrossFade())
                .load(cachedFiles[fileIndex])
                .transform(RoundedCorners(15))
                .error(R.drawable.ic_broken_image_24px)
                .apply(requestOptions)
                .into(imageView)
        } catch (e: Exception) {
            val myToast = Toast.makeText(
                requireContext(),
                "Что-то пошло не так. Попробуйте снова нажать на стрелку",
                Toast.LENGTH_LONG
            )
            myToast.show()
            if (fileIndex < cachedFiles.size) {
                cachedFiles.removeAt(fileIndex)
            }
            if (fileIndex < descriptionsFiles.size) {
                descriptionsFiles.removeAt(fileIndex)
            }
        }
    }

    protected open suspend fun getRequest(): ResponseStatus = withContext(Dispatchers.IO){
        val responseStatus = ResponseStatus()
        launch {
            val request = async {
                val manager = FuelManager()
                val (_, _, result) = manager.request(
                    Method.GET,
                    "https://developerslife.ru/$category/$pageCounter?json=true"
                ).responseJson()
                result
            }
            try {
                responseStatus.setResponse(request.await())
            } catch (e: Exception) {
                responseStatus.setException(e)
            }
        }
        return@withContext responseStatus
    }
}