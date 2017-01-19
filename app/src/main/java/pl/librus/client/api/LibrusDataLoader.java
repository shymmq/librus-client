package pl.librus.client.api;

import android.content.Context;
import android.util.Log;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.AndroidDoneCallback;
import org.jdeferred.android.AndroidExecutionScope;
import org.jdeferred.android.AndroidFailCallback;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;
import org.joda.time.LocalDate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import pl.librus.client.LibrusUtils;
import pl.librus.client.timetable.TimetableUtils;

/**
 * Created by szyme on 24.12.2016. librus-client
 */

public class LibrusDataLoader {

    static public Promise<LibrusData, Object, Object> load(final Context context) {
        final Deferred<LibrusData, Object, Object> deferred = new DeferredObject<>();
        final String cache_filename = "librus_client_cache";

        AsyncManager.runBackgroundTask(new TaskRunnable<Object, LibrusData, Object>() {
            @Override
            public LibrusData doLongOperation(Object o) throws InterruptedException {
                try {
                    FileInputStream fis = context.openFileInput(cache_filename);
                    ObjectInputStream is = new ObjectInputStream(fis);
                    LibrusData cache = (LibrusData) is.readObject();
                    cache.setContext(context);
                    is.close();
                    fis.close();
                    return cache;
                } catch (FileNotFoundException e) {
                    LibrusUtils.log("doLongOperation: File not found.");
                    deferred.reject(null);
                    return null;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void callback(LibrusData librusData) {
                if (librusData != null) {
                    LibrusUtils.log("callback: File loaded successfully");
                    deferred.resolve(librusData);
                }
            }
        });
        return deferred.promise();
    }

    public static Promise<LibrusData, Void, Void> update(final LibrusData librusData, final Context context) {
        LibrusUtils.log("update: Starting update");
        final Deferred<LibrusData, Void, Void> deferred = new DeferredObject<>();
        List<Promise> tasks = new ArrayList<>();
        APIClient client = new APIClient(context);
        librusData.setSchoolWeeks(new ArrayList<SchoolWeek>());
        List<LocalDate> weekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());

        for (LocalDate weekStart : weekStarts) {
            client.getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
                @Override
                public void onDone(SchoolWeek result) {
                    librusData.getSchoolWeeks().add(result);
                    LibrusUtils.log("School week " + result.getWeekStart() + " downloaded");
                }
            });
        }

        final List<Announcement> pendingAnnouncements = new ArrayList<>(); //Add tagging interface to all datamodel classes
        final List<Event> pendingEvents = new ArrayList<>();
        final List<Grade> pendingGrades = new ArrayList<>();
        final LuckyNumber[] pendingLuckyNumber = new LuckyNumber[1];
        tasks.add(client.getAnnouncements().done(new DoneCallback<List<Announcement>>() {
            @Override
            public void onDone(List<Announcement> result) {
                List<Announcement> added = new ArrayList<>(result);
                added.removeAll(librusData.getAnnouncements());
                pendingAnnouncements.addAll(added);

                List<Announcement> removed = new ArrayList<>(librusData.getAnnouncements());
                removed.removeAll(result);
                //TODO handle removed items

                List<Announcement> common = new ArrayList<>(librusData.getAnnouncements());
                common.retainAll(result);
                //TODO handle changed items

                librusData.setAnnouncements(result);
                LibrusUtils.log(result.size() + " announcements downloaded");
            }
        }));
        tasks.add(client.getEvents().done(new DoneCallback<List<Event>>() {
            @Override
            public void onDone(List<Event> result) {
                List<Event> added = new ArrayList<>(result);
                added.removeAll(librusData.getEvents());
                pendingEvents.addAll(added);

                List<Event> removed = new ArrayList<>(librusData.getEvents());
                removed.removeAll(result);
                //TODO handle removed items

                List<Event> common = new ArrayList<>(librusData.getEvents());
                common.retainAll(result);
                //TODO handle changed items

                librusData.setEvents(result);
                LibrusUtils.log("Events downloaded");
            }
        }));
        tasks.add(client.getLuckyNumber().done(new DoneCallback<LuckyNumber>() {
            @Override
            public void onDone(LuckyNumber result) {
                if (librusData.getLuckyNumber() != null && librusData.getLuckyNumber().getLuckyNumberDay().isBefore(result.getLuckyNumberDay()))
                    pendingLuckyNumber[0] = result;
                librusData.setLuckyNumber(result);
                LibrusUtils.log("LNumber downloaded");
            }
        }));
        tasks.add(client.getGrades().done(new DoneCallback<List<Grade>>() {
            @Override
            public void onDone(List<Grade> result) {
                List<Grade> added = new ArrayList<>(result);
                added.removeAll(librusData.getGrades());
                pendingGrades.addAll(added);

                List<Grade> removed = new ArrayList<>(librusData.getGrades());
                removed.removeAll(result);
                //TODO handle removed items

                List<Grade> common = new ArrayList<>(librusData.getGrades());
                common.retainAll(result);
                //TODO handle changed items

                librusData.setGrades(result);
                LibrusUtils.log("Grades downloaded");
            }
        }));
        tasks.add(client.getComments().done(new DoneCallback<List<GradeComment>>() {
            @Override
            public void onDone(List<GradeComment> result) {
                librusData.setGradeComments(result);
                LibrusUtils.log("Grade comments downloaded");
            }
        }));
        tasks.add(client.getAverages().done(new DoneCallback<List<Average>>() {
            @Override
            public void onDone(List<Average> result) {
                librusData.setAverages(result);
                LibrusUtils.log("Averages downloaded");
            }
        }));
        tasks.add(client.getTextGrades().done(new DoneCallback<List<TextGrade>>() {
            @Override
            public void onDone(List<TextGrade> result) {
                librusData.setTextGrades(result);
                LibrusUtils.log("Text grades downloaded");
            }
        }));
        tasks.add(client.getAttendances().done(new DoneCallback<List<Attendance>>() {
            @Override
            public void onDone(List<Attendance> result) {
                librusData.setAttendances(result);
                LibrusUtils.log("Attendances downloaded");
            }
        }));
        tasks.add(client.getPlainLessons().done(new DoneCallback<List<PlainLesson>>() {
            @Override
            public void onDone(List<PlainLesson> result) {
                librusData.setPlainLessons(result);
                LibrusUtils.log("Plain lessons downloaded");
            }
        }));
        DeferredManager dm = new AndroidDeferredManager();
        dm.when(tasks.toArray(new Promise[tasks.size()])).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                NotificationService notificationService = new NotificationService(context, librusData);
                if (librusData.getAccount() != null)
                    notificationService
                            .addAnnouncements(pendingAnnouncements)
                            .addEvents(pendingEvents)
                            .addGrades(pendingGrades)
                            .addLuckyNumber(pendingLuckyNumber[0]);
                deferred.resolve(librusData);
            }
        }).fail(new FailCallback<OneReject>() {
            @Override
            public void onFail(OneReject result) {
                deferred.reject(null);
            }
        });

        return deferred.promise();
    }

    public static Promise<LibrusData, Void, Void> updatePersistent(final LibrusData librusData, final Context context) {
        LibrusUtils.log("updatePersistent: Starting persistent update");
        final Deferred<LibrusData, Void, Void> deferred = new DeferredObject<>();


        update(librusData, context).done(new DoneCallback<LibrusData>() {
            @Override
            public void onDone(LibrusData result) {
                List<Promise> tasks = new ArrayList<>();
                APIClient client = new APIClient(context);
                //Persistent data:
                tasks.add(client.getAccount().done(new DoneCallback<LibrusAccount>() {
                    @Override
                    public void onDone(LibrusAccount result) {
                        librusData.setAccount(result);
                        LibrusUtils.log("Account downloaded");

                    }
                }));
                tasks.add(client.getTeachers().done(new DoneCallback<List<Teacher>>() {
                    @Override
                    public void onDone(List<Teacher> result) {
                        librusData.setTeachers(result);
                        LibrusUtils.log("Teachers downloaded");

                    }
                }));
                tasks.add(client.getSubjects().done(new DoneCallback<List<Subject>>() {
                    @Override
                    public void onDone(List<Subject> result) {
                        librusData.setSubjects(result);
                        LibrusUtils.log("Subjects downloaded");

                    }
                }));
                tasks.add(client.getEventCategories().done(new DoneCallback<List<EventCategory>>() {
                    @Override
                    public void onDone(List<EventCategory> result) {
                        librusData.setEventCategories(result);
                        LibrusUtils.log("EventCat downloaded");

                    }
                }));
                tasks.add(client.getGradeCategories().done(new DoneCallback<List<GradeCategory>>() {
                    @Override
                    public void onDone(List<GradeCategory> result) {
                        librusData.setGradeCategories(result);
                        LibrusUtils.log("GradeCat downlaoded");
                    }
                }));
                tasks.add(client.getAttendanceCategories().done(new DoneCallback<List<AttendanceCategory>>() {
                    @Override
                    public void onDone(List<AttendanceCategory> result) {
                        librusData.setAttendanceCategories(result);
                        LibrusUtils.log("Attendance categories downloaded");
                    }
                }));

                DeferredManager dm = new AndroidDeferredManager();
                dm.when(tasks.toArray(new Promise[tasks.size()])).done(new AndroidDoneCallback<MultipleResults>() {
                    @Override
                    public AndroidExecutionScope getExecutionScope() {
                        return null;
                    }

                    @Override
                    public void onDone(MultipleResults result) {
                        LibrusUtils.log("onDone: Persistent update done");
                        deferred.resolve(librusData);
                    }
                }).fail(new AndroidFailCallback<OneReject>() {
                    @Override
                    public AndroidExecutionScope getExecutionScope() {
                        return null;
                    }

                    @Override
                    public void onFail(OneReject result) {
                        LibrusUtils.log("onFail: Persistent update failed " + result.toString());
                        deferred.reject(null);
                    }
                });
            }
        });
        return deferred.promise();
    }

    static public void save(LibrusData librusData, Context context) {
        try {
            String cache_filename = "librus_client_cache";
            FileOutputStream fos = context.openFileOutput(cache_filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(librusData);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
