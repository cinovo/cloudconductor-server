package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.Directory;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by janweisssieker on 18.11.16.
 */
@Entity
@Table(name = "directory", schema = "cloudconductor")
public class EDirectory extends AModelApiConvertable<Directory> implements IVersionized<Long>, INamed {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private EPackage pkg;
    private String targetPath;
    private String owner;
    private String group;
    private String fileMode;
    private List<EService> dependentServices;

    private Long version;
    private boolean deleted = false;
    private Long origId;


    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "packageid")
    public EPackage getPkg() {
        return this.pkg;
    }

    public void setPkg(EPackage pkg){
        this.pkg = pkg;
    }

    public String getTargetPath() {
        return this.targetPath;
    }

    public void setTargetPath(String targetPath){
        this.targetPath = targetPath;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    @Column(name = "filegroup", nullable = false)
    public String getGroup(){
        return this.group;
    }

    public void setGroup(String group){
        this.group = group;
    }

    public String getFileMode(){
        return this.fileMode;
    }

    public void setFileMode(String fileMode){
        this.fileMode = fileMode;
    }

    @ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(name = "mappingdirectoryservice", schema = "cloudconductor", //
            joinColumns = @JoinColumn(name = "directoryid"), inverseJoinColumns = @JoinColumn(name = "serviceid"))
    public List<EService> getDependentServices() {
        return this.dependentServices;
    }

    public void setDependentServices(List<EService> dependentServices) {
        this.dependentServices = dependentServices;
    }

    @Override
    @Column(nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {this.name = name;}

    @Override
    public boolean isDeleted() {
        return this.deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public void setOrigId(Long id) {
        this.origId = id;
    }

    @Override
    public Long getOrigId() {
        return this.origId;
    }

    @Override
    public Long getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EDirectory)) {
            return false;
        }
        EDirectory other = (EDirectory) obj;
        if (this.getName() == null) {
            return false;
        }
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return (this.getName() == null) ? 0 : this.getName().hashCode();
    }

    @Override
    public IEntity<Long> cloneNew() {
        EDirectory r = new EDirectory();
        r.setDeleted(this.deleted);
        r.setDependentServices(this.dependentServices);
        r.setFileMode(this.fileMode);
        r.setGroup(this.group);
        r.setName(this.name);
        r.setOrigId(this.origId);
        r.setOwner(this.owner);
        r.setPkg(this.pkg);
        r.setTargetPath(this.targetPath);
        r.setVersion(this.version);
        return r;
    }

    @Override
    @Transient
    public Class<Directory> getApiClass() {
        return Directory.class;
    }

    @Override
    @Transient
    public Directory toApi() {
        Directory configFile = super.toApi();
        configFile.setDependentServices(this.namedModelToStringSet(this.dependentServices));
        return configFile;
    }
}
