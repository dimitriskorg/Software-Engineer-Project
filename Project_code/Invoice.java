import java.sql.Date;

public class Invoice {
    private int invoiceID;
    private int customerID;
    private Date issueDate;
    private Date dueDate;
    private double total;
    private String status;
    
    // Constructor
    public Invoice(int invoiceID, int customerID, Date issueDate, Date dueDate, double total, String status) {
        this.invoiceID = invoiceID;
        this.customerID = customerID;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.total = total;
        this.status = status;
    }
    
    // Getters και Setters
    public int getInvoiceID() {
        return invoiceID;
    }
    
    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }
    
    public int getCustomerID() {
        return customerID;
    }
    
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    
    public Date getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // toString
    @Override
    public String toString() {
        return "Invoice{" +
        "invoiceID=" + invoiceID +
        ", customerID=" + customerID +
        ", issueDate=" + issueDate +
        ", dueDate=" + dueDate +
        ", total=" + total +
        ", status='" + status + " " +
        '}';
    }
}