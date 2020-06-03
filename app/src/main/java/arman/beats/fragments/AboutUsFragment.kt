package arman.beats.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import arman.beats.R


class AboutUsFragment : Fragment() {
   var myActivity: Activity?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_about_us, container, false)
        activity?.title = "About Us"
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        myActivity= context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity=activity
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_sort)
        item?.isVisible =false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

}
