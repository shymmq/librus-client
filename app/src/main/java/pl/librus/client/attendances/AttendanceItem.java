package pl.librus.client.attendances;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.api.Attendance;
import pl.librus.client.api.AttendanceCategory;
import pl.librus.client.api.PlainLesson;
import pl.librus.client.api.Subject;
import pl.librus.client.sql.LibrusDbHelper;

class AttendanceItem extends AbstractSectionableItem<AttendanceItem.ViewHolder, AttendanceHeaderItem> {
    private Attendance attendance;
    private final AttendanceCategory category;

    AttendanceItem(AttendanceHeaderItem header, Attendance attendance, AttendanceCategory category) {
        super(header);
        this.attendance = attendance;
        this.category = category;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.attendance_item;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.shortName.setText(category.getShortName());
        Context context = holder.itemView.getContext();
        LibrusDbHelper helper = new LibrusDbHelper(context);
        PlainLesson lesson = helper.getLesson(attendance.getLessonId());
        Subject subject = helper.getSubject(lesson.getSubjectId());
        holder.subject.setText(subject.getName());
        String lessonNumber = context.getString(R.string.lesson) + " " + attendance.getLessonNumber();
        holder.lesson.setText(lessonNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendanceItem that = (AttendanceItem) o;

        return attendance.equals(that.attendance);

    }

    @Override
    public int hashCode() {
        return attendance.hashCode();
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public AttendanceCategory getCategory() {
        return category;
    }

    class ViewHolder extends FlexibleViewHolder {
        TextView subject, lesson, shortName;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            subject = (TextView) view.findViewById(R.id.attendance_item_lesson);
            lesson = (TextView) view.findViewById(R.id.attendance_item_lesson_number);
            shortName = (TextView) view.findViewById(R.id.attendance_item_shortType);
        }
    }
}
