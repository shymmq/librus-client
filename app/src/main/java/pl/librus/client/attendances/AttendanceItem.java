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
import io.reactivex.android.schedulers.AndroidSchedulers;
import pl.librus.client.R;
import pl.librus.client.api.LibrusData;
import pl.librus.client.datamodel.AttendanceWithCategory;
import pl.librus.client.datamodel.FullAttendance;

class AttendanceItem extends AbstractSectionableItem<AttendanceItem.ViewHolder, AttendanceHeaderItem> {
    private final AttendanceWithCategory attendance;
    private ViewHolder holder;

    AttendanceItem(AttendanceHeaderItem header, AttendanceWithCategory attendance) {
        super(header);
        this.attendance = attendance;
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
        this.holder = holder;
        LibrusData.findFullAttendance(attendance)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayInfo);

    }

    private void displayInfo(FullAttendance fullAttendance) {
        holder.shortName.setText(attendance.category().shortName());
        Context context = holder.itemView.getContext();
        String lessonNumber = context.getString(R.string.lesson) + " " + attendance.lessonNumber();
        holder.lesson.setText(lessonNumber);

        holder.subject.setText(fullAttendance.subject().name());
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

    public AttendanceWithCategory getAttendance() {
        return attendance;
    }

    class ViewHolder extends FlexibleViewHolder {
        final TextView subject;
        final TextView lesson;
        final TextView shortName;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            subject = (TextView) view.findViewById(R.id.attendance_item_lesson);
            lesson = (TextView) view.findViewById(R.id.attendance_item_lesson_number);
            shortName = (TextView) view.findViewById(R.id.attendance_item_shortType);
        }
    }
}
