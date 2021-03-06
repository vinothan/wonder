package er.erxtest.model;

import org.apache.log4j.Logger;

public class Employee extends _Employee {

  @SuppressWarnings({ "unused", "hiding" })
  private static Logger log = Logger.getLogger(Employee.class);
  
  @Override
  public void setCompany(Company company) {
    takeStoredValueForKey(company, Employee.COMPANY_KEY);
  }
}
