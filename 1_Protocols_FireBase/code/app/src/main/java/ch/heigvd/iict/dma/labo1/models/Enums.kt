package ch.heigvd.iict.dma.labo1.models

enum class Serialisation {
    JSON, XML, PROTOBUF
}

enum class NetworkType {
    RANDOM, CSD, GPRS, EDGE, UMTS, HSPA, LTE, NR5G
}

enum class Compression {
    DISABLED, DEFLATE
}

enum class Encryption {
    DISABLED, SSL
}