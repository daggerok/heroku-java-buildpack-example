package daggerok.app.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CompanyDTO implements Serializable {
  private static final long serialVersionUID = -5963335829959991004L;
  Long companyId;
  String companyName;
  Set<Object> orders;
}
