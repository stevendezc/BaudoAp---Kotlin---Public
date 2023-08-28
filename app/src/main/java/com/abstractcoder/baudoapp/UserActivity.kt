package com.abstractcoder.baudoapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.databinding.ActivityUserBinding
import com.abstractcoder.baudoapp.recyclers.*
import com.abstractcoder.baudoapp.utils.InfoDialog
import com.abstractcoder.baudoapp.utils.ItemSpacingDecoration
import com.abstractcoder.baudoapp.utils.UserImageDialog
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class UserActivity : FragmentActivity() {
    private val db = FirebaseFirestore.getInstance()

    private lateinit var binding: ActivityUserBinding
    private lateinit var userData: FirebaseUser

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var savedPostAdapter: SavedPostAdapter
    private lateinit var savedPostRecyclerView: RecyclerView
    private var userSavedPosts: ArrayList<SavedPostMain> = ArrayList()

    private lateinit var weekImagelayoutManager: LinearLayoutManager
    private lateinit var weekImageAdapter: ImagePostAdapter
    private lateinit var weekImageRecyclerView: RecyclerView
    private var weekImagePosts: ArrayList<ImagePostMain> = ArrayList()

    private lateinit var recommendedVideosLayoutManager: LinearLayoutManager
    private lateinit var recommendedVideosAdapter: VideoPostAdapter
    private lateinit var recommendedVideoRecyclerView: RecyclerView
    private var recommendedVideoPosts: ArrayList<VideoPostMain> = ArrayList()

    private lateinit var lastImagePost: PostData
    private lateinit var lastlyRecommendedVideos: List<PostData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recover data with bundle
        val bundle = intent.extras
        val email: String = bundle?.getString("email").toString()
        val provider: String = bundle?.getString("provider").toString()
        val name: String = bundle?.getString("name").toString()

        db.collection("users").document(email).get().addOnSuccessListener { user ->
            val myData = user.toObject(FirebaseUser::class.java) ?: FirebaseUser()
            // Update your UI with the new data
            userData = myData
            setRecommendedContent()
            obtainMetrics(userData)
            // Setup incoming data
            setup(email, name, provider)
        }

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weekImagelayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recommendedVideosLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setup(email: String, name: String, provider: String) {
        title = "Pagina de Usuario"

        val userImage = userData.user_pic
        Glide.with(binding.userImage)
            .load(userImage)
            .into(binding.userImage)

        binding.userNameTextView.text = name

        binding.editUserImage.setOnClickListener {
            UserImageDialog(
                userData,
                email
            ).show(supportFragmentManager, "user image dialog")
        }

        binding.settingsButton.setOnClickListener {
            showSettings()
        }

        binding.infoButton.setOnClickListener {
            InfoDialog("perfil").show(supportFragmentManager, "info dialog")
        }

        savedPostRecyclerView = binding.savedPostListRecycler
        savedPostRecyclerView.layoutManager = layoutManager
        savedPostRecyclerView.setHasFixedSize(true)
        savedPostAdapter = SavedPostAdapter(userSavedPosts)
        savedPostRecyclerView.adapter = savedPostAdapter

        weekImageRecyclerView = binding.weeklyImageListRecycler
        weekImageRecyclerView.layoutManager = weekImagelayoutManager
        weekImageRecyclerView.setHasFixedSize(true)
        weekImageAdapter = ImagePostAdapter(weekImagePosts)
        weekImageRecyclerView.adapter = weekImageAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing)
        recommendedVideoRecyclerView = binding.recommendedVideosListRecycler
        recommendedVideoRecyclerView.layoutManager = recommendedVideosLayoutManager
        recommendedVideoRecyclerView.addItemDecoration(ItemSpacingDecoration(spacingInPixels))
        recommendedVideoRecyclerView.setHasFixedSize(true)
        recommendedVideosAdapter = VideoPostAdapter(recommendedVideoPosts)
        recommendedVideoRecyclerView.adapter = recommendedVideosAdapter
    }

    private fun setRecommendedContent() {
        db.collection("posts").get().addOnSuccessListener { posts ->
            val postDataList = mutableListOf<PostData>()
            for (document in posts) {
                val postData = document.toObject(PostData::class.java)
                postData.id = document.id
                postDataList.add(postData)
            }
            // Setup subfragments
            println("postDataList: $postDataList")
            var savedPosts = postDataList.filter { item -> userData.saved_posts.contains(item.id) }
            val parsedSavedPosts = savedPosts.map { savedPost ->
                SavedPostMain(
                    savedPost.id,
                    savedPost.thumbnail,
                    if (savedPost.title != "") savedPost.title else savedPost.location,
                    userData.liked_posts.contains(savedPost.id)
                ) }
            if (parsedSavedPosts.size == 0) {
                binding.savedContent.visibility = LinearLayout.GONE
            }
            userSavedPosts.addAll(parsedSavedPosts)
            val organizedPosts = postDataList.sortedByDescending { post -> post.creation_date }.toCollection(ArrayList())
            println("organizedPosts: $organizedPosts")
            println("image posts: ${organizedPosts.filter { organizedPost -> organizedPost.type == "image" }.size}")
            lastImagePost = organizedPosts.filter { organizedPost -> organizedPost.type == "image" }[0]
            weekImagePosts.add(ImagePostMain(lastImagePost.id, Uri.parse(lastImagePost.thumbnail),
                Uri.parse(lastImagePost.main_media), lastImagePost.title, lastImagePost.author,
                lastImagePost.location, lastImagePost.description, lastImagePost.commentaries!!,
                lastImagePost.creation_date))
            println("video posts: ${organizedPosts.filter { organizedPost -> organizedPost.type == "video" }.size}")
            val videoPosts = organizedPosts.filter { organizedPost -> organizedPost.type == "video" }
            lastlyRecommendedVideos = if (videoPosts.size == 3) videoPosts.subList(0, 3) else listOf<PostData>()
            for (video in lastlyRecommendedVideos) {
                recommendedVideoPosts.add(
                    VideoPostMain(video.id, Uri.parse(video.main_media),
                        Uri.parse(video.thumbnail), video.title.toString(), video.description.toString(),
                        video.category.toString()))
            }
        }
    }
    private fun obtainMetrics(user: FirebaseUser) {
        db.collection("posts").get().addOnSuccessListener {
            val postDataList = mutableListOf<PostData>()
            for (document in it) {
                val postData = document.toObject(PostData::class.java)
                postData.id = document.id
                postDataList.add(postData)
            }
            val postsArrayList: ArrayList<PostData> = ArrayList()
            val totalCommentaries = user.commentaries.size
            val totalReactions = user.reactions.size
            val totalPositiveReactions = user.reactions.size
            val totalSavedPosts = user.saved_posts.size
            var likedAmbientalPosts = 0
            var likedMemoryPosts = 0
            var likedGenderPosts = 0
            postsArrayList.addAll(postDataList)
            for (reaction in user.reactions) {
                val postInfo = postsArrayList.find { post -> post.id == reaction }
                when (postInfo?.category) {
                    "medio_ambiente" -> likedAmbientalPosts += 1
                    "memoria" -> likedMemoryPosts += 1
                    "genero" -> likedGenderPosts += 1
                }
            }
            setMetrics(UserMetrics(
                totalCommentaries,
                totalReactions,
                totalPositiveReactions,
                totalSavedPosts,
                likedAmbientalPosts,
                likedMemoryPosts,
                likedGenderPosts
            ))
        }
    }

    private fun renderCategoryMetrics(renderParams: JSONObject, userMetrics: UserMetrics, mainCategoryPercentage: Int, secondaryCategoryPercentage: Int, thirdCategoryPercentage: Int) {
        val mainColor = renderParams.getString("mainCategoryContainer")
        val mainProgressDrawableId = resources.getIdentifier(renderParams.getString("mainCategoryProgress"), "drawable", packageName)
        val mainIconDrawableId = resources.getIdentifier(renderParams.getString("mainCategoryIcon"), "drawable", packageName)
        val secondaryColor = renderParams.getString("secondaryCategoryContainer")
        val secondaryProgressDrawableId = resources.getIdentifier(renderParams.getString("secondaryCategoryProgress"), "drawable", packageName)
        val secondaryIconDrawableId = resources.getIdentifier(renderParams.getString("secondaryCategoryIcon"), "drawable", packageName)
        val thirdColor = renderParams.getString("thirdCategoryContainer")
        val thirdProgressDrawableId = resources.getIdentifier(renderParams.getString("thirdCategoryProgress"), "drawable", packageName)
        val thirdIconDrawableId = resources.getIdentifier(renderParams.getString("thirdCategoryIcon"), "drawable", packageName)

        // Main Liked Category
        println("mainCategoryTitle: ${renderParams.getString("mainCategoryTitle")}")
        binding.mainCategoryContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor(mainColor))
        binding.mainCategoryProgress.progressDrawable = ContextCompat.getDrawable(this, mainProgressDrawableId)
        binding.mainCategoryProgress.progress = 100 - mainCategoryPercentage
        binding.mainCategoryIcon.setImageDrawable(ContextCompat.getDrawable(this, mainIconDrawableId))
        binding.mainCategoryPercentage.text = "$mainCategoryPercentage%"
        binding.mainCategoryPercentage.setTextColor(Color.parseColor(mainColor))
        binding.mainCategoryTitle.text = renderParams.getString("mainCategoryTitle")
        binding.mainCategoryTitle.setTextColor(Color.parseColor(mainColor))
        binding.mainCategoryStats.text = "Interacciones totales: ${userMetrics.totalReactions}\n" +
                "Reacciones positivas: ${userMetrics.totalPositiveReactions}\n" +
                "Total comentarios: ${userMetrics.totalCommentaries}\n" +
                "Total guardados: ${userMetrics.totalSavedPosts}"
        binding.mainCategoryStats.setTextColor(Color.parseColor(mainColor))
        // Secondary Liked Category
        println("secondaryCategoryTitle: ${renderParams.getString("secondaryCategoryTitle")}")
        binding.secondaryCategoryContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor(secondaryColor))
        binding.secondaryCategoryProgress.progressDrawable = ContextCompat.getDrawable(this, secondaryProgressDrawableId)
        binding.secondaryCategoryProgress.progress = 100 - secondaryCategoryPercentage
        binding.secondaryCategoryIcon.setImageDrawable(ContextCompat.getDrawable(this, secondaryIconDrawableId))
        binding.secondaryCategoryPercentage.text = "$secondaryCategoryPercentage%"
        binding.secondaryCategoryPercentage.setTextColor(Color.parseColor(secondaryColor))
        binding.secondaryCategoryTitle.text = renderParams.getString("secondaryCategoryTitle")
        binding.secondaryCategoryTitle.setTextColor(Color.parseColor(secondaryColor))
        // Tertiary Liked Category
        println("thirdCategoryTitle: ${renderParams.getString("thirdCategoryTitle")}")
        binding.thirdCategoryContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor(thirdColor))
        binding.thirdCategoryProgress.progressDrawable = ContextCompat.getDrawable(this, thirdProgressDrawableId)
        binding.thirdCategoryProgress.progress = 100 - thirdCategoryPercentage
        binding.thirdCategoryIcon.setImageDrawable(ContextCompat.getDrawable(this, thirdIconDrawableId))
        binding.thirdCategoryPercentage.text = "$thirdCategoryPercentage%"
        binding.thirdCategoryPercentage.setTextColor(Color.parseColor(thirdColor))
        binding.thirdCategoryTitle.text = renderParams.getString("thirdCategoryTitle")
        binding.thirdCategoryTitle.setTextColor(Color.parseColor(thirdColor))
    }

    private fun obtainCategoryOpts(variant: String, userMetrics: UserMetrics, mainCategoryPercentage: Int, secondaryCategoryPercentage: Int, thirdCategoryPercentage: Int) {
        var json: String? = null
        var jsonObject: JSONObject? = null
        try {
            val inputStream: InputStream = assets.open("userLikedCategories.json")
            json = inputStream.bufferedReader().use { it.readText() }
            jsonObject = JSONObject(json)
            println("variant: $variant")
            val outputJson = jsonObject.getJSONObject(variant)
            println(outputJson)
            renderCategoryMetrics(
                outputJson,
                userMetrics,
                mainCategoryPercentage,
                secondaryCategoryPercentage,
                thirdCategoryPercentage
            )
        } catch (e: IOException) {}
    }

    private fun setMetrics(userMetrics: UserMetrics) {
        val likedAmbientalPosts = ((userMetrics.likedAmbientalPosts.toFloat() / userMetrics.totalReactions.toFloat()) * 100).toInt()
        val likedMemoryPosts = ((userMetrics.likedMemoryPosts.toFloat() / userMetrics.totalReactions.toFloat()) * 100).toInt()
        val likedGenderPosts = ((userMetrics.likedGenderPosts.toFloat() / userMetrics.totalReactions.toFloat()) * 100).toInt()
        if (likedAmbientalPosts > likedMemoryPosts &&
            likedAmbientalPosts > likedGenderPosts) {
            if (likedMemoryPosts > likedGenderPosts) obtainCategoryOpts("1", userMetrics, likedAmbientalPosts, likedMemoryPosts, likedGenderPosts)
            else obtainCategoryOpts("2", userMetrics, likedAmbientalPosts, likedGenderPosts, likedMemoryPosts)
            return
        }
        if (likedMemoryPosts > likedAmbientalPosts &&
            likedMemoryPosts > likedGenderPosts) {
            if (likedAmbientalPosts > likedGenderPosts) obtainCategoryOpts("3", userMetrics, likedMemoryPosts, likedAmbientalPosts, likedGenderPosts)
            else obtainCategoryOpts("4", userMetrics, likedMemoryPosts, likedGenderPosts, likedAmbientalPosts)
            return
        }
        if (likedGenderPosts > likedMemoryPosts &&
            likedGenderPosts > likedAmbientalPosts) {
            if (likedAmbientalPosts > likedMemoryPosts) obtainCategoryOpts("5", userMetrics, likedGenderPosts, likedAmbientalPosts, likedMemoryPosts)
            else obtainCategoryOpts("6", userMetrics, likedGenderPosts, likedMemoryPosts, likedAmbientalPosts)
            return
        }
    }

    private fun showSettings() {
        val configIntent = Intent(this, ConfigActivity::class.java)
        startActivity(configIntent)
    }
}