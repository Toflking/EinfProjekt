import java.sql.Timestamp;

public class Meal {
   private int id;
   private int external_id;
   private String name;
   private int category_id;
   private int area_id;
   private String instructions;
   private String thumb;
   private String youtube;
   private String source;
   private String tags;
   private Timestamp created_at;
   private Timestamp updated_at;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getExternal_id() {
      return external_id;
   }

   public void setExternal_id(int external_id) {
      this.external_id = external_id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getCategory_id() {
      return category_id;
   }

   public void setCategory_id(int category_id) {
      this.category_id = category_id;
   }

   public int getArea_id() {
      return area_id;
   }

   public void setArea_id(int area_id) {
      this.area_id = area_id;
   }

   public String getInstructions() {
      return instructions;
   }

   public void setInstructions(String instructions) {
      this.instructions = instructions;
   }

   public String getThumb() {
      return thumb;
   }

   public void setThumb(String thumb) {
      this.thumb = thumb;
   }

   public String getYoutube() {
      return youtube;
   }

   public void setYoutube(String youtube) {
      this.youtube = youtube;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public String getTags() {
      return tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }

   public Timestamp getCreated_at() {
      return created_at;
   }

   public void setCreated_at(Timestamp created_at) {
      this.created_at = created_at;
   }

   public Timestamp getUpdated_at() {
      return updated_at;
   }

   public void setUpdated_at(Timestamp updated_at) {
      this.updated_at = updated_at;
   }

   @Override
   public String toString() {
      return "Meal{" +
              "id=" + id +
              ", external_id=" + external_id +
              ", name='" + name + '\'' +
              ", category_id=" + category_id +
              ", area_id=" + area_id +
              ", instructions='" + instructions + '\'' +
              ", thumb='" + thumb + '\'' +
              ", youtube='" + youtube + '\'' +
              ", source='" + source + '\'' +
              ", tags='" + tags + '\'' +
              ", created_at=" + created_at +
              ", updated_at=" + updated_at +
              '}';
   }
}
