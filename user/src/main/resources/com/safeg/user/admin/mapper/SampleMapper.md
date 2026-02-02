<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!-- namespace="매퍼 인터페이스 경로" --> 
<mapper namespace="com.aloha.bodyguard.mapper.SampleMapper">
    
</mapper>


1. users 테이블 (회원 정보)
회원 (경호원 또는 의뢰인)의 기본 정보를 담는 테이블이야. 경호원이나 의뢰인 모두 이 테이블의 id를 참조할 수 있어.

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 고유 ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '사용자 로그인 ID',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '이메일 주소',
    phone_number VARCHAR(20) COMMENT '연락처',
    name VARCHAR(50) NOT NULL COMMENT '실명 또는 닉네임',
    role VARCHAR(20) DEFAULT 'CLIENT' COMMENT '사용자 역할 (GUARD, CLIENT, ADMIN 등)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시'
);s

2. companies 테이블 (업체 정보)
campaigns 테이블의 brand, contact, address 필드를 별도 테이블로 분리해서 중복을 줄이고, 여러 캠페인이 하나의 업체에 의해 운영될 때 효율적으로 관리할 수 있도록 했어.

CREATE TABLE companies (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '업체 고유 ID',
    name VARCHAR(100) NOT NULL COMMENT '업체명',
    contact_number VARCHAR(20) NOT NULL COMMENT '업체 대표 연락처',
    address VARCHAR(255) COMMENT '업체 주소',
    business_license_number VARCHAR(50) UNIQUE COMMENT '사업자 등록 번호',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시'
);

3. guides 테이블 (인솔자 정보)
campaigns 테이블의 guide_select, guide_contact 필드를 분리했어. 한 인솔자가 여러 캠페인에 참여할 수 있고, 인솔자 정보가 변경될 때 guides 테이블만 수정하면 돼.

CREATE TABLE guides (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '인솔자 고유 ID',
    name VARCHAR(50) NOT NULL COMMENT '인솔자 이름',
    phone_number VARCHAR(20) NOT NULL COMMENT '인솔자 연락처',
    email VARCHAR(100) UNIQUE COMMENT '인솔자 이메일',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시'
);

4. categories 테이블 (캠페인 카테고리)
campaigns 테이블의 category 필드를 분리하여 카테고리의 일관성을 유지하고 관리하기 쉽게 만들었어.

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '카테고리 고유 ID',
    name VARCHAR(50) UNIQUE NOT NULL COMMENT '카테고리 이름 (예: VIP경호, 행사경비, 개인신변보호)',
    description VARCHAR(255) COMMENT '카테고리 설명',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시'
);

5. campaign_types 테이블 (경호 진행 구분)
campaigns 테이블의 type 필드를 분리했어. '경호 진행 구분'의 종류가 많지 않고 고정적일 때 유용해.
CREATE TABLE campaign_types (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '경호 진행 구분 고유 ID',
    name VARCHAR(50) UNIQUE NOT NULL COMMENT '경호 진행 구분 이름 (예: 단기, 장기, 1회성)',
    description VARCHAR(255) COMMENT '경호 진행 구분 설명',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시'
);

6. campaigns 테이블 (캠페인/모집 공고 정보)
핵심 테이블로, 위에 분리된 테이블들을 외래키로 참조해. 날짜 필드도 1NF에 맞춰 분리하고, limit은 예약어와 겹치지 않게 recruit_limit으로 변경했어. image, has_review, applicants는 별도 테이블에서 관리해.

CREATE TABLE campaigns (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '캠페인/모집 공고 고유 ID',
    company_id INT NOT NULL COMMENT '업체 ID (FK: companies.id)',
    guide_id INT COMMENT '인솔자 ID (FK: guides.id)',
    category_id INT NOT NULL COMMENT '카테고리 ID (FK: categories.id)',
    type_id INT NOT NULL COMMENT '경호 진행 구분 ID (FK: campaign_types.id)',
    title VARCHAR(200) NOT NULL COMMENT '캠페인명',
    recruit_limit INT NOT NULL COMMENT '모집인원 수',
    service_address VARCHAR(255) NOT NULL COMMENT '서비스 제공 주소',
    note TEXT COMMENT '안내사항',
    apply_start_date DATE NOT NULL COMMENT '신청 시작일',
    apply_end_date DATE NOT NULL COMMENT '신청 종료일',
    event_start_date DATE NOT NULL COMMENT '행사 시작일',
    event_end_date DATE NOT NULL COMMENT '행사 종료일',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '캠페인 상태 (ACTIVE, CLOSED 등)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시',
    
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (guide_id) REFERENCES guides(id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    FOREIGN KEY (type_id) REFERENCES campaign_types(id) ON DELETE RESTRICT
);

7. campaign_images 테이블 (캠페인 이미지)
캠페인에 여러 이미지가 있을 수 있으므로 별도 테이블로 분리했어.

CREATE TABLE campaign_images (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '이미지 고유 ID',
    campaign_id INT NOT NULL COMMENT '캠페인 ID (FK: campaigns.id)',
    image_url VARCHAR(255) NOT NULL COMMENT '이미지 URL',
    order_number INT DEFAULT 0 COMMENT '이미지 표시 순서',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE
);

8. campaign_applications 테이블 (캠페인 지원자)
applicants 필드 대신 누가 어떤 캠페인에 지원했는지 구체적으로 기록하는 테이블이야. users 테이블의 id를 외래키로 참조하여 어떤 사용자가 지원했는지 알 수 있어.

CREATE TABLE campaign_applications (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '지원 내역 고유 ID',
    campaign_id INT NOT NULL COMMENT '캠페인 ID (FK: campaigns.id)',
    user_id INT NOT NULL COMMENT '지원자 (사용자) ID (FK: users.id)',
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '지원일시',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '지원 상태 (PENDING, APPROVED, REJECTED)',
    
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (campaign_id, user_id) COMMENT '한 사용자는 한 캠페인에 한 번만 지원 가능'
);

9. reviews 테이블 (리뷰/후기)
has_review 필드 대신 상세한 리뷰 정보를 관리하는 테이블이야.

CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '리뷰 고유 ID',
    campaign_id INT NOT NULL COMMENT '리뷰 대상 캠페인 ID (FK: campaigns.id)',
    user_id INT NOT NULL COMMENT '리뷰 작성자 ID (FK: users.id)',
    rating INT CHECK (rating >= 1 AND rating <= 5) NOT NULL COMMENT '별점 (1점 ~ 5점)',
    comment TEXT COMMENT '리뷰 내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시',
    
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
