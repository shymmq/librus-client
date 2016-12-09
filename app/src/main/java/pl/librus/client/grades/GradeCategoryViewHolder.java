package pl.librus.client.grades;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import pl.librus.client.R;

/**
 * Created by szyme on 09.12.2016. librus-client
 */

class GradeCategoryViewHolder extends ParentViewHolder {

    private TextView title;
    private View background;

    GradeCategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        background = itemView.findViewById(R.id.grade_category_item_background);
        title = (TextView) itemView.findViewById(R.id.grade_category_item_title);
    }

    void bind(GradeCategory category) {
        title.setText(category.getTitle());
    }
}
