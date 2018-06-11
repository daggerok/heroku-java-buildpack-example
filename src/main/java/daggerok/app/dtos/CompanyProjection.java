package daggerok.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyProjection {
  private static final long serialVersionUID = -781610242287919869L;
  Long companyId;
  String companyName;
}
