package in.sigma.SplitDash.repository;


import in.sigma.SplitDash.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepo extends JpaRepository<Bill, String> {
}
