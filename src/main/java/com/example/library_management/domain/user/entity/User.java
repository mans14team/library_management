package com.example.library_management.domain.user.entity;

import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookReservation.entity.BookReservation;
import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.membership.entity.Membership;
import com.example.library_management.domain.review.entity.Review;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String userName;

    @Column(length = 30)
    private String phone;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @OneToOne(mappedBy = "user")
    private Membership membership;

    @OneToMany(mappedBy = "user")
    private List<BookRental> bookRentalList = new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<BookReservation> bookReservationList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<Review> reviewList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<RoomReserve> roomReserveList = new ArrayList<>();

    public User(String email, String password, String userName, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.role = userRole;
        this.status = UserStatus.ACTIVE;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void delete() {
        this.status = UserStatus.DELETED;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
