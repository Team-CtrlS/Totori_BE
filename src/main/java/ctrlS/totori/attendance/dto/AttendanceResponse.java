package ctrlS.totori.attendance.dto;

public record AttendanceResponse(
        boolean newlyAttended, // 오늘 처음 출석하면 true, 아니면 false
        int totalAttendanceDays
) {
    public static AttendanceResponse of(boolean newlyAttended, int totalAttendanceDays) {
        return new AttendanceResponse(newlyAttended, totalAttendanceDays);
    }
}
