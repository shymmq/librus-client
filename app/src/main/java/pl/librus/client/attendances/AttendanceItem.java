package pl.librus.client.attendances;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceType;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.sql.LibrusDbHelper;

class AttendanceItem extends AbstractSectionableItem<AttendanceItem.ViewHolder, AttendanceHeaderItem> {
    private final AttendanceType category;
    private Attendance attendance;

    AttendanceItem(AttendanceHeaderItem header, Attendance attendance, AttendanceType category) {
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
        String lessonNumber = context.getString(R.string.lesson) + " " + attendance.getLessonNumber();
        holder.lesson.setText(lessonNumber);
        LibrusDbHelper helper = new LibrusDbHelper(context);
        try {
            Dao<PlainLesson, String> lessonDao = helper.getDao(PlainLesson.class);
            Dao<Subject, String> subjectDao = helper.getDao(Subject.class);
            PlainLesson lesson = lessonDao.queryForId(attendance.getLesson().getId());
            Subject subject = subjectDao.queryForId(lesson.getSubject().getId());
            holder.subject.setText(subject.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public AttendanceType getCategory() {
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
