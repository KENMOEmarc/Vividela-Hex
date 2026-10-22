package ken.vivid.adapter.output.persistence.jpaEntities.article;

import jakarta.persistence.*;
import ken.vivid.domain.dto.ArticleStatus;
import ken.vivid.domain.dto.ClothingType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "clothing_type", nullable = false)
    private ClothingType clothingType;

    @Column(length = 20)
    private String size;

    @Column(length = 50)
    private String fabric;

    @Column(length = 50)
    private String color;

    @Column(length = 255)
    private String distinction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "article_services", joinColumns = @JoinColumn(name = "article_id"))
    @OrderColumn(name = "position")
    private List<ArticleServiceJpaEmbeddable> services = new ArrayList<>();
}
