package com.abstractcoder.baudoapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.databinding.ActivityUserBinding
import com.abstractcoder.baudoapp.recyclers.*
import com.abstractcoder.baudoapp.utils.Firestore
import com.abstractcoder.baudoapp.utils.InfoDialog
import com.abstractcoder.baudoapp.utils.ItemSpacingDecoration
import com.abstractcoder.baudoapp.utils.UserImageDialog
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class UserActivity : FragmentActivity() {
    private val firestore = Firestore()

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

        firestore.activateSubscribers(this, email!!)
        firestore.userLiveData.observe(this, Observer { user ->
            // Update your UI with the new data
            println("userData in User activity: ${user}")
            userData = user
            obtainMetrics(userData)
            // Setup incoming data
            setup(email, name, provider)
        })

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weekImagelayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recommendedVideosLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        firestore.postsLiveData.observe(this, Observer { posts ->
            Log.d(ContentValues.TAG, "posts on User activity: $posts")
            // Setup subfragments
            var savedPosts = posts.filter { item -> userData.saved_posts.contains(item.id) }
            val parsedSavedPosts = savedPosts.map { SavedPostMain(
                it.id,
                it.thumbnail,
                if (it.title != "") it.title else it.location,
                userData.liked_posts.contains(it.id)
            ) }
            if (parsedSavedPosts.size == 0) {
                binding.savedContent.visibility = LinearLayout.GONE
            }
            userSavedPosts.addAll(parsedSavedPosts)
            val organizedPosts = posts.sortedByDescending { it.creation_date }.toCollection(ArrayList())
            lastImagePost = organizedPosts.filter { it.type == "image" }[0]
            weekImagePosts.add(ImagePostMain(lastImagePost.id, Uri.parse(lastImagePost.thumbnail),
                Uri.parse(lastImagePost.main_media), lastImagePost.title, lastImagePost.author,
                lastImagePost.location, lastImagePost.description, lastImagePost.commentaries!!,
                lastImagePost.creation_date))
            lastlyRecommendedVideos = organizedPosts.filter { it.type == "video" }.subList(0, 3)
            for (video in lastlyRecommendedVideos) {
                recommendedVideoPosts.add(
                    VideoPostMain(video.id, Uri.parse(video.main_media),
                    Uri.parse(video.thumbnail), video.title.toString(), video.description.toString(),
                    video.category.toString()))
            }
        })

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

    private fun obtainMetrics(user: FirebaseUser) {
        firestore.postsLiveData.observe(this, Observer { posts ->
            val postsArrayList: ArrayList<PostData> = ArrayList()
            val totalCommentaries = user.commentaries.size
            val totalReactions = user.reactions.size
            val totalPositiveReactions = user.reactions.filter { reaction -> reaction.type == "likes" }.size
            val totalSavedPosts = user.saved_posts.size
            var likedAmbientalPosts = 0
            var likedMemoryPosts = 0
            var likedGenderPosts = 0
            postsArrayList.addAll(posts)
            for (reaction in user.reactions) {
                val postInfo = postsArrayList.find { post -> post.id == reaction.post }
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
        })
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
        binding.mainCategoryContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor(mainColor))
        binding.mainCategoryProgress.progressDrawable = ContextCompat.getDrawable(this, mainProgressDrawableId)
        binding.mainCategoryProgress.progress = 100 - mainCategoryPercentage
        binding.mainCategoryIcon.setImageDrawable(ContextCompat.getDrawable(this, mainIconDrawableId))
        binding.mainCategoryPercentage.text = "$mainCategoryPercentage%"
        binding.mainCategoryPercentage.setTextColor(Color.parseColor(mainColor))
        binding.mainCategoryTitle.text = renderParams.getString("mainCategoryTitle")
        binding.mainCategoryTitle.setTextColor(Color.parseColor(mainColor))
        binding.mainCategoryStats.text = "Interacciones Totales: ${userMetrics.totalReactions}\n" +
                "Reacciones positivas: ${userMetrics.totalPositiveReactions}\n" +
                "Total comentarios: ${userMetrics.totalCommentaries}\n" +
                "Total guardados: ${userMetrics.totalSavedPosts}"
        binding.mainCategoryStats.setTextColor(Color.parseColor(mainColor))
        // Secondary Liked Category
        binding.secondaryCategoryContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor(secondaryColor))
        binding.secondaryCategoryProgress.progressDrawable = ContextCompat.getDrawable(this, secondaryProgressDrawableId)
        binding.secondaryCategoryProgress.progress = 100 - secondaryCategoryPercentage
        binding.secondaryCategoryIcon.setImageDrawable(ContextCompat.getDrawable(this, secondaryIconDrawableId))
        binding.secondaryCategoryPercentage.text = "$secondaryCategoryPercentage%"
        binding.secondaryCategoryPercentage.setTextColor(Color.parseColor(secondaryColor))
        binding.secondaryCategoryTitle.text = renderParams.getString("secondaryCategoryTitle")
        binding.secondaryCategoryTitle.setTextColor(Color.parseColor(secondaryColor))
        // Tertiary Liked Category
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
            val outputJson = jsonObject.getJSONObject(variant)
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
        }
        if (likedMemoryPosts > likedAmbientalPosts &&
            likedMemoryPosts > likedGenderPosts) {
            if (likedAmbientalPosts > likedGenderPosts) obtainCategoryOpts("3", userMetrics, likedMemoryPosts, likedAmbientalPosts, likedGenderPosts)
            else obtainCategoryOpts("4", userMetrics, likedMemoryPosts, likedGenderPosts, likedAmbientalPosts)
        } else {
            if (likedAmbientalPosts > likedMemoryPosts) obtainCategoryOpts("5", userMetrics, likedGenderPosts, likedAmbientalPosts, likedMemoryPosts)
            else obtainCategoryOpts("6", userMetrics, likedGenderPosts, likedMemoryPosts, likedAmbientalPosts)
        }
    }

    private fun showSettings() {
        val configIntent = Intent(this, ConfigActivity::class.java)
        startActivity(configIntent)
    }
}