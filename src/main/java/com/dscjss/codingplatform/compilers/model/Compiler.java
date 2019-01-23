package com.dscjss.codingplatform.compilers.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compiler")
public class Compiler {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "slug")
    private String slug;

    @Column(name = "ace_editor_mode")
    private String aceEditorMode;

    @OneToOne
    private Version version;

    @Column(name = "default_time_limit")
    private int defaultTimeLimit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAceEditorMode() {
        return aceEditorMode;
    }

    public void setAceEditorMode(String aceEditorMode) {
        this.aceEditorMode = aceEditorMode;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public int getDefaultTimeLimit() {
        return defaultTimeLimit;
    }

    public void setDefaultTimeLimit(int defaultTimeLimit) {
        this.defaultTimeLimit = defaultTimeLimit;
    }
}
