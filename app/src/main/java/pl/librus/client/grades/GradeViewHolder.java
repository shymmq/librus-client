package pl.librus.client.grades;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import java.util.Locale;

import pl.librus.client.R;
import pl.librus.client.api.Grade;
import pl.librus.client.api.GradeCategory;

/**
 * Created by szyme on 09.12.2016. librus-client
 */

class GradeViewHolder extends ChildViewHolder {

    private TextView grade, title, subtitle;

    GradeViewHolder(@NonNull View itemView) {
        super(itemView);
        grade = (TextView) itemView.findViewById(R.id.grade_item_grade);
        title = (TextView) itemView.findViewById(R.id.grade_item_title);
        subtitle = (TextView) itemView.findViewById(R.id.grade_item_subtitle);
    }

    void bind(Grade g, GradeCategory c) {
        grade.setText(g.getGrade());
        title.setText(c.getName());
        subtitle.setText(g.getDate().toString("d MMM.", new Locale("pl")));
    }
}
