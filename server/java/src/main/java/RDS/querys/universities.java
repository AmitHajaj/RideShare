package RDS.querys;
import RDS.models.University;

public class universities {
    public String addUniversity(University u) {
        return "INSERT INTO \"public\".\"rs_universities\" (\"university_Id\", \"geoLocation_Id\", \"university_name\") " +
                "VALUES " +
                "('"+u.getUniversityId()+"', '"+u.getGeoLocation_Id()+"', '"+u.getUniversity_name()+"');";
    }
    public String updateUniversity(University u) {
        return "UPDATE \"public\".\"rs_universities\" SET " +
                "\"university_name\" = '"+u.getUniversity_name()+"', " +
                "\"geoLocation_Id\" = '"+u.getGeoLocation_Id()+"' " +
                "WHERE \"university_Id\" = '"+u.getUniversityId()+"';";
    }
    public String deleteUniversityById(University u) {
        return "DELETE FROM \"public\".\"rs_universities\" WHERE \"university_Id\" = '"+u.getUniversityId()+"';";
    }
    public String getUniversityById(University u) {
        return "SELECT * FROM \"public\".\"rs_universities\" WHERE \"geoLocation_Id\" = '"+u.getUniversityId()+"';";
    }
    public String getAllUniversities() {
        return "SELECT * FROM \"public\".\"rs_universities\";";
    }
}