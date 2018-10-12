DROP TABLE IF EXISTS "Association";
DROP TABLE IF EXISTS "objectDB";
DROP TABLE IF EXISTS "GO";
DROP TABLE IF EXISTS "Gene";
DROP TABLE IF EXISTS "Taxon";
DROP TABLE IF EXISTS "Reference";
DROP TABLE IF EXISTS "WithFrom";
DROP TABLE IF EXISTS "Evidence";
DROP TABLE IF EXISTS "Quantifier";

CREATE TABLE "objectDB" (
    "objectDBID" SERIAL   NOT NULL,
    "uniprotID" varchar(15)   NOT NULL,
    "objType" varchar(50)   NOT NULL,
    "Synonyms" varchar(500)   NOT NULL,
    "Name" varchar(500)   NOT NULL,
    CONSTRAINT "pk_objectDB" PRIMARY KEY (
        "objectDBID"
     ),
    CONSTRAINT "uc_objectDB_Synonyms" UNIQUE (
        "uniprotID"
    )
);

CREATE TABLE "GO" (
    "GOnumberID" serial  NOT NULL,
    "GOID" varchar(15)   NOT NULL,
    "Ontology" varchar(15)   NOT NULL,
    CONSTRAINT "pk_GO" PRIMARY KEY (
        "GOnumberID"
     ),
    CONSTRAINT "uc_GO_GOID" UNIQUE (
        "GOID"
    )
);

CREATE TABLE "Association" (
    "AssociationID" serial  NOT NULL,
    "GOnumberID" int   NOT NULL,
    "QuantifierID" int   NULL,
    "objectDBID" int   NOT NULL,
    "EvidenceID" int   NOT NULL,
    "GeneID" int   NOT NULL,
    "TaxonID" int   NOT NULL,
    "ReferenceID" int   NOT NULL,
    "WithFromID" int   NULL,
    "Version" date   NOT NULL,
    CONSTRAINT "pk_Association" PRIMARY KEY (
        "AssociationID"
     )
);

-- Table documentation comment 1 (try the PDF/RTF export)
-- Table documentation comment 2
CREATE TABLE "Gene" (
    "GeneID" serial  NOT NULL,
    -- Field documentation comment 3
    "Symbol" varchar(50)   NOT NULL,
    CONSTRAINT "pk_Gene" PRIMARY KEY (
        "GeneID"
     ),
    CONSTRAINT "uc_Gene_Symbol" UNIQUE (
        "Symbol"
    )
);

CREATE TABLE "Taxon" (
    "TaxonID" int  NOT NULL,
    "Name" varchar(500)   NULL,
    CONSTRAINT "pk_Taxon" PRIMARY KEY (
        "TaxonID"
     ),
    CONSTRAINT "uc_Taxon_Name" UNIQUE (
        "Name"
    )
);

CREATE TABLE "Evidence" (
    "EvidenceID" serial  NOT NULL,
    "Name" varchar(5)   NOT NULL,
    CONSTRAINT "pk_Evidence" PRIMARY KEY (
        "EvidenceID"
     ),
    CONSTRAINT "uc_Evidence_Name" UNIQUE (
        "Name"
    )
);

CREATE TABLE "Quantifier" (
    "QuantifierID" serial NOT NULL,
    "Name" varchar(20)   NOT NULL,
    CONSTRAINT "pk_Quantifier" PRIMARY KEY (
        "QuantifierID"
     ),
    CONSTRAINT "uc_Quantifier_Name" UNIQUE (
        "Name"
    )
);

CREATE TABLE "WithFrom" (
    "WithFromID" serial NOT NULL,
    "Name" varchar(2000)   NOT NULL,
    CONSTRAINT "pk_WithFrom" PRIMARY KEY (
        "WithFromID"
     )
);

CREATE TABLE "Reference" (
    "ReferenceID" serial   NOT NULL,
    "Name" varchar(50)   NOT NULL,
    CONSTRAINT "pk_Reference" PRIMARY KEY (
        "ReferenceID"
     ),
    CONSTRAINT "uc_Reference_Name" UNIQUE (
        "Name"
    )
);

ALTER TABLE "Association" ADD CONSTRAINT "fk_Association_GOnumberID" FOREIGN KEY("GOnumberID")
REFERENCES "GO" ("GOnumberID");

ALTER TABLE "Association" ADD CONSTRAINT "fk_Association_QuantifierID" FOREIGN KEY("QuantifierID")
REFERENCES "Quantifier" ("QuantifierID");

ALTER TABLE "Association" ADD CONSTRAINT "fk_Association_objectDBID" FOREIGN KEY("objectDBID")
REFERENCES "objectDB" ("objectDBID");

ALTER TABLE "Association" ADD CONSTRAINT "fk_Association_EvidenceID" FOREIGN KEY("EvidenceID")
REFERENCES "Evidence" ("EvidenceID");

ALTER TABLE "Association" ADD CONSTRAINT "fk_Association_GeneID" FOREIGN KEY("GeneID")
REFERENCES "Gene" ("GeneID");

ALTER TABLE "Association" ADD CONSTRAINT "fk_Association_TaxonID" FOREIGN KEY("TaxonID")
REFERENCES "Taxon" ("TaxonID");

ALTER TABLE "Association" ADD CONSTRAINT "fk_Association_ReferenceID" FOREIGN KEY("ReferenceID")
REFERENCES "Reference" ("ReferenceID");

ALTER TABLE "Association" ADD CONSTRAINT "fk_Association_WithFromID" FOREIGN KEY("WithFromID")
REFERENCES "WithFrom" ("WithFromID");

ALTER TABLE "objectDB"
  ADD CONSTRAINT "uq_objectDB" UNIQUE("uniprotID", "Name","Synonyms");

