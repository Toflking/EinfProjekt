package model;

import java.sql.Timestamp;

// Klasse für Meal Objekte
public class Meal {
   // Klassenvariablen
   private int id;
   private int external_id;
   private String name;
   private Integer category_id;
   private Integer area_id;
   private String instructions;
   private String thumb;
   private String youtube;
   private String source;
   private String tags;
   private Timestamp created_at;
   private Timestamp updated_at;
   private int created_by_user_id;

   // Getter und Setter
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

   public Integer getCategory_id() {
      return category_id;
   }

   public void setCategory_id(Integer category_id) {
      this.category_id = category_id;
   }

   public Integer getArea_id() {
      return area_id;
   }

   public void setArea_id(Integer area_id) {
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

   public int getCreated_by_user_id() {
      return created_by_user_id;
   }

   public void setCreated_by_user_id(int created_by_user_id) {
      this.created_by_user_id = created_by_user_id;
   }

   // toString methode
   @Override
   public String toString() {
      return "model.Meal{" +
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
