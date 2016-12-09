package pl.librus.client.grades;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import pl.librus.client.R;
import pl.librus.client.api.Grade;

/**
 * Created by szyme on 09.12.2016. librus-client
 */

class GradeViewHolder extends ChildViewHolder {

    private TextView title;

    GradeViewHolder(@NonNull View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.grade_category_item_title);
    }

    void bind(Grade grade) {
        title.setText(grade.getGrade());
    }
}
