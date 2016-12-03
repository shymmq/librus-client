package pl.librus.client.timetable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Locale;

import pl.librus.client.R;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.SchoolDay;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private final SchoolDay schoolDay;

    public LessonAdapter(SchoolDay schoolDay) {
        this.schoolDay = schoolDay;
//        Log.d(TAG, "Data received in lesson adapter: " + schoolDay.getLessons().entrySet().toString());
    }

    @Override
    public LessonAdapter.LessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lesson_item, parent, false);
        return new LessonViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(LessonViewHolder holder, int position) {

        final Lesson lesson = schoolDay.getLesson(position + 1);
        final Lesson prevLesson = schoolDay.getLesson(position);
        final Context context = holder.background.getContext();

        if (lesson == null) {

            //EMPTY LESSON

            holder.lessonNumber.setText(position + 1 + ".");
            holder.badge.setVisibility(View.GONE);
            holder.lessonTeacher.setVisibility(View.GONE);
            holder.lessonSubject.setVisibility(View.GONE);
            holder.lessonEmpty.setVisibility(View.VISIBLE);
            holder.background.setAlpha(0.4f);
        } else {

            //LESSON

            holder.lessonNumber.setText(lesson.getLessonNumber() + ".");
            holder.lessonSubject.setText(lesson.getSubject().getName());
            holder.lessonTeacher.setText(lesson.getTeacher().getName());

            if (lesson.isCanceled()) {

                //canceled

                holder.lessonSubject.setPaintFlags(holder.lessonSubject.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.lessonTeacher.setPaintFlags(holder.lessonTeacher.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.background.setAlpha(0.5f);
                holder.badge.setVisibility(View.VISIBLE);
                holder.badgeText.setText("odwołane");
                holder.badgeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel_black_24dp, context.getTheme()));
            } else if (lesson.getEvent() != null) {

                //event

                holder.badge.setVisibility(View.VISIBLE);
                holder.badgeText.setText(lesson.getEvent().getCategory());
                holder.badgeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_event_black_24dp, context.getTheme()));
            } else if (lesson.isSubstitution()) {

                //substitution

                holder.badge.setVisibility(View.VISIBLE);
                holder.badgeText.setText("zastępstwo");
                holder.badgeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_swap_horiz_black_24dp, context.getTheme()));
            } else {

                //none

                holder.badge.setVisibility(View.GONE);
            }

            if (LocalDate.now().equals(lesson.getDate())) {

                //if today:

                LocalTime timeNow = LocalTime.now();
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

                if (timeNow.isAfter(lesson.getEndTime()) && prefs.getBoolean("greyOutFinishedLessons", true)) {

                    //lesson finished

                    holder.background.setAlpha(0.5f);

                } else if (!lesson.isCanceled() && prefs.getBoolean("currentLessonBold", true)) {
                    if (timeNow.isBefore(lesson.getEndTime())) {
                        if (prevLesson == null || prevLesson.isCanceled() || lesson.getLessonNumber() == 1 || timeNow.isAfter(prevLesson.getEndTime())) {
                            holder.lessonSubject.setTypeface(holder.lessonSubject.getTypeface(), Typeface.BOLD);
                        }
                    }
                }
            }
            if (!lesson.isCanceled()) {
                holder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(context).title(lesson.getSubject().getName()).positiveText("Zamknij");

                        LayoutInflater inflater = LayoutInflater.from(context);
                        View details = inflater.inflate(R.layout.lesson_details, null);

                        TextView teacher = (TextView) details.findViewById(R.id.details_teacher);
                        TextView orgTeacher = (TextView) details.findViewById(R.id.details_org_teacher);
                        TextView orgSubject = (TextView) details.findViewById(R.id.details_org_subject);
                        TextView subject = (TextView) details.findViewById(R.id.details_subject);
                        RelativeLayout subjectContainer = (RelativeLayout) details.findViewById(R.id.details_subject_container);
                        TextView date = (TextView) details.findViewById(R.id.details_date);
                        TextView startTime = (TextView) details.findViewById(R.id.details_start_time);
                        TextView endTime = (TextView) details.findViewById(R.id.details_end_time);
                        TextView lessonNumber = (TextView) details.findViewById(R.id.details_lesson_number);
                        LinearLayout event = (LinearLayout) details.findViewById(R.id.event);

                        teacher.setText(lesson.getTeacher().getName());
                        date.setText(lesson.getDate().toString("EEEE, d MMMM yyyy", new Locale("pl")));
                        startTime.setText(lesson.getStartTime().toString("HH:mm"));
                        endTime.setText(" - " + lesson.getEndTime().toString("HH:mm"));
                        lessonNumber.setText(lesson.getLessonNumber() + ". lekcja");
                        if (lesson.getEvent() != null) {
                            TextView eventName = (TextView) details.findViewById(R.id.details_event_name);
                            TextView eventDescription = (TextView) details.findViewById(R.id.details_event_description);
                            event.setVisibility(View.VISIBLE);
                            eventName.setText(lesson.getEvent().getCategory());
                            eventDescription.setText(lesson.getEvent().getDescription());
                        } else {
                            event.setVisibility(View.GONE);
                        }
                        if (lesson.isSubstitution()) {
                            if (lesson.getTeacher() != lesson.getOrgTeacher()) {
                                orgTeacher.setText(lesson.getOrgTeacher().getName() + " -> ");
                            }
                            if (lesson.getSubject() != lesson.getOrgSubject()) {
                                orgSubject.setText(lesson.getOrgSubject().getName() + " -> ");
                                subject.setText(lesson.getSubject().getName());
                            }
                        } else {
                            orgTeacher.setVisibility(View.GONE);
                            subjectContainer.setVisibility(View.GONE);
                        }
                        MaterialDialog dialog = builder.customView(details, true).show();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return schoolDay.size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout background;
        final TextView
                lessonSubject;
        final TextView lessonEmpty;
        final TextView lessonTeacher;
        final TextView lessonNumber;
        final TextView badgeText;
        final CardView badge;
        final ImageView badgeIcon;

        LessonViewHolder(View root) {
            super(root);
            lessonSubject = (TextView) root.findViewById(R.id.lessonSubject);
            lessonTeacher = (TextView) root.findViewById(R.id.lessonTeacher);
            lessonNumber = (TextView) root.findViewById(R.id.lessonNumber);
            lessonEmpty = (TextView) root.findViewById(R.id.lessonEmpty);
            badgeText = (TextView) root.findViewById(R.id.badgeText);
            badge = (CardView) root.findViewById(R.id.badge);
            badgeIcon = (ImageView) root.findViewById(R.id.badgeIcon);
            background = (LinearLayout) root.findViewById(R.id.background);
        }
    }
}
