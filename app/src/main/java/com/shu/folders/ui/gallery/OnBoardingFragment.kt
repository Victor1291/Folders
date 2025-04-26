package com.shu.folders.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.shu.folders.R
import com.shu.folders.databinding.FragmentOnBoardingBinding
import com.shu.folders.models.MediaStoreImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var onboardingItemAdapter: OnboardingItemAdapter
    private lateinit var indicatorsContainer: LinearLayout

    private val viewModel by viewModels<OnViewModel>()

    private val args: OnBoardingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)

        // ViewCompat.setTransitionName(binding.cardView, "cardView${args.data.position}")

      /*  sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)

        *//* ChangeBounds().apply {
         duration = 750
     }*//*
        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = args.data
        setOnboardingItems(data.listImages, data.position)

        // setupIndicators()
        //  setCurrentIndicator(0)

        /* binding.skip.setOnClickListener {

         }*/
    }

    private fun setOnboardingItems(onboardingItems: List<MediaStoreImage>, page: Int) {
        onboardingItemAdapter = OnboardingItemAdapter(
            onboardingItems = onboardingItems
        )
        binding.viewpager.adapter = onboardingItemAdapter

        //SmoothScroll false - disable scroll animation
        binding.viewpager.setCurrentItem(page, false)

        // Откладываем переход до полной загрузки изображения
        postponeEnterTransition()
        binding.viewpager.doOnPreDraw {
            startPostponedEnterTransition()
        }

        /*binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(page, positionOffset, positionOffsetPixels)
               // onPageScrollStateChanged(SCROLL_STATE_IDLE)

               // onPageScrollStateChanged(SCROLL_STATE_SETTLING)

            //}

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
              //  onPageScrollStateChanged(SCROLL_STATE_DRAGGING)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                //  setCurrentIndicator(position)
            //}
        })*/
        /* (binding.viewpager.getChildAt(0) as RecyclerView) = false */

        //val recyclerView = binding.viewpager.getChildAt(0) as RecyclerView

        // recyclerView.layoutManager?.scrollToPosition(page)
        /*  binding.viewpager.apply {
              currentItem = page
              //registerOnPageChangeCallback(pageChangeCallback)
              (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
          }*/


        //  val itemCount = binding.viewpager.adapter?.itemCount ?: 0
// attach scroll listener
        /* recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
             override fun onScrolled(
                 recyclerView: RecyclerView, dx: Int, dy: Int) {
                 super.onScrolled(recyclerView, dx, dy)
                 val firstItemVisible
                         = layoutManager.findFirstVisibleItemPosition()
                 val lastItemVisible
                         = layoutManager.findLastVisibleItemPosition()
                 if (firstItemVisible == (itemCount - 1) && dx > 0) {
                     recyclerView.scrollToPosition(1)
                 } else if (lastItemVisible == 0 && dx < 0) {
                     recyclerView.scrollToPosition(itemCount - 2)
                 }
             }
         })*/
    }

    /* private fun setupIndicators() {
         indicatorsContainer = binding.indicatorrsContainer
         val indicators = arrayOfNulls<ImageView>(onboardingItemAdapter.itemCount)
         val layoutParams: LinearLayout.LayoutParams =
             LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
         layoutParams.setMargins(8, 0, 8, 0)
         for (i in indicators.indices) {
             indicators[i] = ImageView(context)
             indicators[i]?.let {
                 it.setImageDrawable(
                     ContextCompat.getDrawable(
                         requireContext(), R.drawable.indicator_inactive_background
                     )
                 )
                 it.layoutParams = layoutParams
                 indicatorsContainer.addView(it)
             }
         }
     }*/

    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_inactive_background
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}