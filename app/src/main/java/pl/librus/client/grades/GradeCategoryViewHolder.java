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
    private TextView content;
    private View root, divider;

    GradeCategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.two_line_list_item_root);
        divider = itemView.findViewById(R.id.two_line_list_item_divider);
        title = (TextView) itemView.findViewById(R.id.two_line_list_item_title);
        content = (TextView) itemView.findViewById(R.id.two_line_list_item_content);

    }

    void bind(GradesFragment.GradeListCategory category) {
        title.setText(category.getTitle());
        int size = category.getChildList().size();
        String gradeCount;
        if (size == 0) gradeCount = "Brak ocen";
        else if (size == 1) gradeCount = "1 ocena";
        else if (2 <= size && size <= 4) gradeCount = size + " oceny";
        else if (5 <= size) gradeCount = size + " ocen";
        else gradeCount = "Oceny: " + size;
        content.setText(gradeCount);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded()) {
//                    divider.setVisibility(View.VISIBLE);
                    collapseView();
                } else {
//                    divider.setVisibility(View.INVISIBLE);
                    expandView();
                }
            }
        });
    }

    @Override
    public boolean shouldItemViewClickToggleExpansion() {
        return false;
    }
}
