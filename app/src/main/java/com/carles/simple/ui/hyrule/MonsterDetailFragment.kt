package com.carles.simple.ui.hyrule

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.carles.simple.R
import com.carles.simple.common.ui.BaseFragment
import com.carles.simple.common.ui.ERROR
import com.carles.simple.common.ui.LOADING
import com.carles.simple.common.ui.SUCCESS
import com.carles.simple.databinding.FragmentMonsterDetailBinding
import com.carles.simple.model.MonsterDetail
import com.carles.simple.ui.hyrule.ErrorDialogFragment.Companion.EXTRA_RETRY
import com.carles.simple.ui.hyrule.ErrorDialogFragment.Companion.REQUEST_CODE_RETRY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonsterDetailFragment : BaseFragment<FragmentMonsterDetailBinding>() {

    private val viewModel: MonsterDetailViewModel by viewModels()

    override val progress: View
        get() = binding.monsterProgress.progress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResultListener()
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMonsterDetailBinding {
        return FragmentMonsterDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMonsterDetail()
    }

    private fun setResultListener() {
        setFragmentResultListener(REQUEST_CODE_RETRY) { _, bundle ->
            if (bundle.getBoolean(EXTRA_RETRY)) {
                viewModel.retry()
            } else {
                viewModel.onErrorDismissed()
            }
        }
    }

    private fun observeMonsterDetail() {
        viewModel.monsterDetail.observe(viewLifecycleOwner) { result ->
            when (result.state) {
                SUCCESS -> {
                    hideProgress()
                    result.data?.let { monster -> showMonsterDetail(monster) }
                }
                ERROR -> {
                    hideProgress()
                    viewModel.onErrorDisplayed(result.message ?: getString(R.string.error_server_response))
                }
                LOADING -> showProgress()
            }
        }
    }

    private fun showMonsterDetail(monster: MonsterDetail) {
        (activity as AppCompatActivity).supportActionBar?.title = monster.name
        binding.monsterContent.visibility = VISIBLE
        binding.monsterLocations.text = monster.commonLocations
        binding.monsterDescription.text = monster.description
        Glide.with(this)
            .load(monster.image)
            .centerInside()
            .listener(object : RequestListener<Drawable?> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.monsterImage.tag = null
                    binding.monsterImageUrl.visibility = VISIBLE
                    binding.monsterImageUrl.text = monster.image
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.monsterImage.tag = getString(R.string.tag_monster_image_loaded)
                    return false
                }
            })
            .into(binding.monsterImage)
    }

    companion object {
        const val EXTRA_ID = "extraId"
    }
}