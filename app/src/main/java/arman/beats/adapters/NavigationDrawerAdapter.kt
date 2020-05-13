package arman.beats.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import arman.beats.R
import arman.beats.activities.MainActivity
import arman.beats.fragments.AboutUsFragment
import arman.beats.fragments.FavoriteFragment
import arman.beats.fragments.MainScreenFragment
import arman.beats.fragments.SettingsFragment



class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImages:IntArray, _context:Context)
    : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>(){
    var contentList:ArrayList<String>?=null
    var getImages:IntArray?=null
    var mContext:Context?=null
    init {
        this.contentList=_contentList
        this.getImages=_getImages
        this.mContext=_context

    }

    override fun onBindViewHolder(holder:NavViewHolder, position: Int) {
        holder?.icon_Get?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.text_Get?.setText(contentList?.get(position))
        holder?.contentHolder?.setOnClickListener({
            if(position==0){
                val mainScreenFragment= MainScreenFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, mainScreenFragment)
                    .commit()
            }else if (position==1){
                val favoriteFragment= FavoriteFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, favoriteFragment)
                    .addToBackStack(null)
                    .commit()
            }else if (position==2) {
                val settingsFragment = SettingsFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, settingsFragment)
                    .addToBackStack(null)
                    .commit()
            }else {
                val aboutusFragment = AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, aboutusFragment)
                    .addToBackStack(null)
                    .commit()
            }
            MainActivity.Statified.drawerLayout?.closeDrawers()
        })
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        var itemView = LayoutInflater.from(parent?.context)
            .inflate(R.layout.row_custom_navigationdrawer, parent, false)
        val returnThis = NavViewHolder(itemView)
        return returnThis
    }

    override fun getItemCount(): Int {
        return (contentList as ArrayList).size

    }

   inner class NavViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
       var icon_Get: ImageView? = null
       var text_Get: TextView? = null
       var contentHolder: RelativeLayout? = null

       init {
           icon_Get = itemView?.findViewById(R.id.icon_navdrawer)
           text_Get = itemView?.findViewById(R.id.text_navdrawer)
           contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
       }
   }

}