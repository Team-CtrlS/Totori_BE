package ctrlS.totori.attendance.dto;

public record AttendanceResponse(
        boolean attendedToday,
        int totalAttendanceDays
) {
    public static AttendanceResponse of(boolean attendedToday, int totalAttendanceDays) {
        return new AttendanceResponse(attendedToday, totalAttendanceDays);
    }
}
