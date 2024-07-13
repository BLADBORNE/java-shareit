package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.constraint.CreatedItem;
import ru.practicum.shareit.item.constraint.UpdatedItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(groups = CreatedItem.class)
    @Pattern(regexp = ".*[^ ].*",groups = UpdatedItem.class)
    @Column(name = "name", nullable = false)
    private String name;
    @NotBlank(groups = CreatedItem.class)
    @Pattern(regexp = ".*[^ ].*",groups = UpdatedItem.class)
    @Column(name = "description", nullable = false)
    private String description;
    @NotNull(groups = CreatedItem.class)
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Transient
    private ItemBooking lastBooking;
    @Transient
    private ItemBooking nextBooking;
    @Transient
    private List<Comment> comments = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}