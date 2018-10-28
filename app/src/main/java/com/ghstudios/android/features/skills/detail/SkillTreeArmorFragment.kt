package com.ghstudios.android.features.skills.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.ClickListeners.ArmorClickListener
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.util.applyArguments
import com.ghstudios.android.util.setImageAsset

/**
 * Fragment used to display a list of armor that have at least one point in a particular skill
 */
class SkillTreeArmorFragment : ListFragment() {

    companion object {
        private val ARG_SKILL = "SKILLTREE_SKILL"
        private val ARG_TYPE = "SKILLTREE_TYPE"

        @JvmStatic fun newInstance(skill: Long, armorType: String): SkillTreeArmorFragment {
            return SkillTreeArmorFragment().applyArguments {
                putLong(ARG_SKILL, skill)
                putString(ARG_TYPE, armorType)
            }
        }
    }

    /**
     * ViewModel belonging to the parent activity
     */
    private val parentViewModel by lazy {
        ViewModelProviders.of(activity!!).get(SkillDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // required for ListFragment

        val mType = arguments!!.getString(ARG_TYPE)

        val adapter = ArmorToSkillTreeListAdapter(context!!)
        listAdapter = adapter

        parentViewModel.observeArmorsWithSkill(this, mType, Observer {
            if (it != null) {
                adapter.updateItems(it)
            }
        })
    }

    /**
     * Internal cursor adapter to display armors that provide the specified skill
     */
    private class ArmorToSkillTreeListAdapter(context: Context)
        : ArrayAdapter<ItemToSkillTree>(context, R.layout.fragment_skill_item_listitem) {

        fun updateItems(newItems: List<ItemToSkillTree>) {
            clear()
            addAll(newItems)
            notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = when (convertView) {
                null -> {
                    val inflater = LayoutInflater.from(context)
                    inflater.inflate(R.layout.fragment_skill_item_listitem, parent, false)
                }
                else -> convertView
            }

            // Get the skill for the current row
            val skill = getItem(position)
            val armor = skill.item as Armor

            // Set up the text view
            val skillItemImageView = view.findViewById<ImageView>(R.id.item_image)
            val skillItemTextView = view.findViewById<TextView>(R.id.item)
            val skillAmtTextView = view.findViewById<TextView>(R.id.amt)

            skillItemImageView.setImageAsset(armor)
            skillItemTextView.text = armor.name
            skillAmtTextView.text = skill.points.toString()

            view.tag = armor.id
            view.setOnClickListener(ArmorClickListener(context, armor))

            return view
        }
    }
}
